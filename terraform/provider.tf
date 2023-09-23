provider "aws" {
  region = "us-east-1"
  profile = "tf_execution_profile"
  shared_credentials_files = ["~/.aws/credentials"]
}

terraform {
  backend "remote" {
    hostname     = "app.terraform.io"
    organization = "konstde00"
    workspaces {
      name = "todo_app_be"
    }
  }
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.68"
    }
  }
}
