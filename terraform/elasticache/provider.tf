terraform {

  required_version = "0.14.7"

  backend "remote" {
    hostname     = "app.terraform.io"
    organization = "konstde00"
    workspaces {
      prefix = "todo_app_be_${var.environment_name}"
    }
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
}

provider "aws" {
  region = "us-west-2"

  default_tags {
    tags = var.tags
  }
}
