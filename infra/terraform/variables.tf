variable "project_id" {
  description = "GCP project that holds every resource below."
  type        = string
}

variable "region" {
  description = "Region for Cloud Run, Cloud SQL and Artifact Registry. us-central1 qualifies for the Cloud Run free tier."
  type        = string
  default     = "us-central1"
}

variable "service_name" {
  description = "Name shared by the Cloud Run service, its service account and the image repository."
  type        = string
  default     = "secure-chat-api"
}

variable "image_tag" {
  description = "Tag of the API image in Artifact Registry to deploy (scripts/deploy.sh passes the git SHA)."
  type        = string
  default     = "latest"
}

variable "cors_allowed_origins" {
  description = "Comma-separated browser origins allowed to call the API."
  type        = string
  default     = "http://localhost:5173"
}

variable "db_tier" {
  description = "Cloud SQL machine tier. db-f1-micro is the smallest shared-core tier."
  type        = string
  default     = "db-f1-micro"
}

variable "deletion_protection" {
  description = "Guards the database against `terraform destroy`. Set false only when you mean to tear it down."
  type        = bool
  default     = true
}

variable "billing_account_id" {
  description = "Billing account for the monthly budget alert (e.g. 013DD6-XXXXXX-XXXXXX). Empty skips the budget."
  type        = string
  default     = ""
}

variable "monthly_budget_usd" {
  description = "Budget alert threshold in US dollars; emails go to the billing account admins at 50%, 90% and 100%."
  type        = number
  default     = 15
}
