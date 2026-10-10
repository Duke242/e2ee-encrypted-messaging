terraform {
  required_version = ">= 1.6"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 8.4"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }
}

provider "google" {
  project = var.project_id
  region  = var.region

  # Bill API quota (e.g. the Budgets API) to this project instead of the
  # caller's default, which user credentials do not have.
  user_project_override = true
  billing_project       = var.project_id
}
