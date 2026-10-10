#!/usr/bin/env bash
# Build the API image for Cloud Run (linux/amd64), push it to Artifact
# Registry tagged with the git commit, then roll it out with Terraform.
# Usage: [AUTO_APPROVE=1] scripts/deploy.sh   (reads project_id/region from infra/terraform/terraform.tfvars)
set -euo pipefail
cd "$(dirname "$0")/.."
TF_DIR=infra/terraform

PROJECT_ID=$(sed -n 's/^project_id *= *"\(.*\)"/\1/p' "$TF_DIR/terraform.tfvars")
REGION=$(sed -n 's/^region *= *"\(.*\)"/\1/p' "$TF_DIR/terraform.tfvars")
REGION="${REGION:-us-central1}"
TAG=$(git rev-parse --short HEAD)
REPO="${REGION}-docker.pkg.dev/${PROJECT_ID}/secure-chat-api"
IMAGE="${REPO}/secure-chat-api:${TAG}"

terraform -chdir="$TF_DIR" init -input=false -backend-config="bucket=${PROJECT_ID}-tfstate" >/dev/null

# The registry must exist before the first push.
terraform -chdir="$TF_DIR" apply -input=false -auto-approve \
  -target=google_project_service.apis -target=google_artifact_registry_repository.images

gcloud auth configure-docker "${REGION}-docker.pkg.dev" --quiet
docker buildx build --platform linux/amd64 -t "$IMAGE" --push backend

terraform -chdir="$TF_DIR" apply -input=false ${AUTO_APPROVE:+-auto-approve} -var "image_tag=${TAG}"
echo "Deployed ${IMAGE}"
terraform -chdir="$TF_DIR" output api_url
