#!/usr/bin/env bash
# One-time setup for a fresh GCP project: APIs Terraform needs before it can
# run, and the private, versioned bucket that holds Terraform state.
# Usage: scripts/bootstrap-gcp.sh <project-id> [region]
set -euo pipefail
PROJECT_ID="${1:?usage: bootstrap-gcp.sh <project-id> [region]}"
REGION="${2:-us-central1}"
BUCKET="gs://${PROJECT_ID}-tfstate"

gcloud services enable storage.googleapis.com billingbudgets.googleapis.com \
  cloudbilling.googleapis.com cloudresourcemanager.googleapis.com --project "$PROJECT_ID"

if ! gcloud storage buckets describe "$BUCKET" --project "$PROJECT_ID" >/dev/null 2>&1; then
  gcloud storage buckets create "$BUCKET" --project "$PROJECT_ID" --location "$REGION" \
    --uniform-bucket-level-access --public-access-prevention
  gcloud storage buckets update "$BUCKET" --versioning
fi
echo "State bucket ready: $BUCKET"
