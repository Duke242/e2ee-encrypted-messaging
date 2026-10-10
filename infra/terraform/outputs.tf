output "api_url" {
  description = "Public URL of the API."
  value       = google_cloud_run_v2_service.api.uri
}

output "image_repository" {
  description = "Where scripts/deploy.sh pushes images."
  value       = "${var.region}-docker.pkg.dev/${var.project_id}/${google_artifact_registry_repository.images.repository_id}"
}

output "sql_connection_name" {
  description = "Cloud SQL instance connection name (project:region:instance)."
  value       = google_sql_database_instance.postgres.connection_name
}
