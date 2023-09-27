provider "aws" {
  region = "us-east-1"
}

terraform {
  required_version = ">= 1.3"
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
      version = "5.17.0"
    }
  }
}
