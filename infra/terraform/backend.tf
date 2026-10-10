# Remote state in a private, versioned Cloud Storage bucket (created once by
# scripts/bootstrap-gcp.sh). State holds generated secrets, so it never lives
# on a laptop or in git. Pass the bucket at init:
#   terraform init -backend-config="bucket=<project-id>-tfstate"
terraform {
  backend "gcs" {
    prefix = "secure-chat/state"
  }
}
