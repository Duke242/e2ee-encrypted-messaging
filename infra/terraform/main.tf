# Secure Chat API on Google Cloud:
#
#   Artifact Registry  ->  Cloud Run (Spring Boot container)  ->  Cloud SQL (PostgreSQL 16)
#                               |
#                         Secret Manager (JWT key, DB password)
#
# Cloud Run runs as a dedicated service account that may only connect to the
# database and read its two secrets (least privilege). The database has no
# authorized networks: the only way in is the Cloud SQL connector, which
# authenticates with that service account's IAM identity over TLS.

locals {
  apis = [
    "run.googleapis.com",
    "sqladmin.googleapis.com",
    "artifactregistry.googleapis.com",
    "secretmanager.googleapis.com",
    "iam.googleapis.com",
  ]
  image = "${var.region}-docker.pkg.dev/${var.project_id}/${google_artifact_registry_repository.images.repository_id}/${var.service_name}:${var.image_tag}"
}

resource "google_project_service" "apis" {
  for_each           = toset(local.apis)
  service            = each.value
  disable_on_destroy = false
}

# --- Container images ---

resource "google_artifact_registry_repository" "images" {
  repository_id = var.service_name
  location      = var.region
  format        = "DOCKER"
  description   = "Images for ${var.service_name}"

  # Keep the five newest images; older ones are deleted so storage stays free.
  cleanup_policies {
    id     = "keep-recent"
    action = "KEEP"
    most_recent_versions {
      keep_count = 5
    }
  }
  cleanup_policies {
    id     = "delete-old"
    action = "DELETE"
    condition {
      older_than = "2592000s" # 30 days
    }
  }

  depends_on = [google_project_service.apis]
}

# --- Database ---

resource "google_sql_database_instance" "postgres" {
  name                = "${var.service_name}-db"
  database_version    = "POSTGRES_16"
  region              = var.region
  deletion_protection = var.deletion_protection

  settings {
    tier              = var.db_tier
    edition           = "ENTERPRISE"
    availability_type = "ZONAL"
    disk_type         = "PD_SSD"
    disk_size         = 10
    disk_autoresize   = false

    ip_configuration {
      ipv4_enabled = true # reachable only through the IAM-authenticated connector; no authorized networks
      ssl_mode     = "ENCRYPTED_ONLY"
    }

    backup_configuration {
      enabled    = true
      start_time = "07:00" # 3am Eastern
    }
  }

  depends_on = [google_project_service.apis]
}

resource "google_sql_database" "app" {
  name     = "secure_chat"
  instance = google_sql_database_instance.postgres.name
}

resource "random_password" "db" {
  length  = 32
  special = false
}

resource "google_sql_user" "app" {
  name     = "secure_chat_app"
  instance = google_sql_database_instance.postgres.name
  password = random_password.db.result
}

# --- Secrets ---

# 64 random bytes, base64-encoded: what JwtTokenUtil requires for HS512.
resource "random_bytes" "jwt_key" {
  length = 64
}

resource "google_secret_manager_secret" "jwt" {
  secret_id = "${var.service_name}-jwt-secret"
  replication {
    auto {}
  }
  depends_on = [google_project_service.apis]
}

resource "google_secret_manager_secret_version" "jwt" {
  secret      = google_secret_manager_secret.jwt.id
  secret_data = random_bytes.jwt_key.base64
}

resource "google_secret_manager_secret" "db_password" {
  secret_id = "${var.service_name}-db-password"
  replication {
    auto {}
  }
  depends_on = [google_project_service.apis]
}

resource "google_secret_manager_secret_version" "db_password" {
  secret      = google_secret_manager_secret.db_password.id
  secret_data = random_password.db.result
}

# --- Identity: least privilege for the running service ---

resource "google_service_account" "api" {
  account_id   = var.service_name
  display_name = "Cloud Run runtime for ${var.service_name}"
}

resource "google_project_iam_member" "api_sql_client" {
  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${google_service_account.api.email}"
}

resource "google_secret_manager_secret_iam_member" "api_reads_jwt" {
  secret_id = google_secret_manager_secret.jwt.id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.api.email}"
}

resource "google_secret_manager_secret_iam_member" "api_reads_db_password" {
  secret_id = google_secret_manager_secret.db_password.id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${google_service_account.api.email}"
}

# --- The API ---

resource "google_cloud_run_v2_service" "api" {
  name                = var.service_name
  location            = var.region
  ingress             = "INGRESS_TRAFFIC_ALL"
  deletion_protection = false

  template {
    service_account = google_service_account.api.email

    # Scale to zero when idle (free tier); cap at 2 so a traffic spike cannot exhaust the small database.
    scaling {
      min_instance_count = 0
      max_instance_count = 2
    }

    containers {
      image = local.image

      resources {
        limits = {
          cpu    = "1"
          memory = "1Gi"
        }
        cpu_idle          = true # CPU only while handling requests
        startup_cpu_boost = true # faster JVM cold starts
      }

      ports {
        container_port = 8080
      }

      env {
        name  = "DATABASE_URL"
        value = "jdbc:postgresql:///${google_sql_database.app.name}?cloudSqlInstance=${google_sql_database_instance.postgres.connection_name}&socketFactory=com.google.cloud.sql.postgres.SocketFactory"
      }
      env {
        name  = "DATABASE_USERNAME"
        value = google_sql_user.app.name
      }
      env {
        name = "DATABASE_PASSWORD"
        value_source {
          secret_key_ref {
            secret  = google_secret_manager_secret.db_password.secret_id
            version = "latest"
          }
        }
      }
      env {
        name = "JWT_SECRET"
        value_source {
          secret_key_ref {
            secret  = google_secret_manager_secret.jwt.secret_id
            version = "latest"
          }
        }
      }
      env {
        name  = "CORS_ALLOWED_ORIGINS"
        value = var.cors_allowed_origins
      }
      env {
        name  = "JAVA_OPTS"
        value = "-XX:MaxRAMPercentage=75 -XX:TieredStopAtLevel=1"
      }

      # Spring Boot Actuator probes (public, see SecurityConfig).
      startup_probe {
        http_get {
          path = "/actuator/health/readiness"
        }
        initial_delay_seconds = 10
        period_seconds        = 5
        failure_threshold     = 24 # up to ~2 minutes for a cold JVM
        timeout_seconds       = 3
      }
      liveness_probe {
        http_get {
          path = "/actuator/health/liveness"
        }
        period_seconds  = 30
        timeout_seconds = 3
      }
    }
  }

  depends_on = [
    google_project_iam_member.api_sql_client,
    google_secret_manager_secret_iam_member.api_reads_jwt,
    google_secret_manager_secret_iam_member.api_reads_db_password,
    google_secret_manager_secret_version.jwt,
    google_secret_manager_secret_version.db_password,
  ]
}

# The API is public; it does its own JWT authentication.
resource "google_cloud_run_v2_service_iam_member" "public" {
  name     = google_cloud_run_v2_service.api.name
  location = google_cloud_run_v2_service.api.location
  role     = "roles/run.invoker"
  member   = "allUsers"
}

# --- Cost guardrail ---

resource "google_billing_budget" "monthly" {
  count           = var.billing_account_id == "" ? 0 : 1
  billing_account = var.billing_account_id
  display_name    = "${var.service_name} monthly budget"

  budget_filter {
    projects = ["projects/${data.google_project.this.number}"]
  }

  amount {
    specified_amount {
      currency_code = "USD"
      units         = tostring(var.monthly_budget_usd)
    }
  }

  threshold_rules {
    threshold_percent = 0.5
  }
  threshold_rules {
    threshold_percent = 0.9
  }
  threshold_rules {
    threshold_percent = 1.0
  }
}

data "google_project" "this" {}
