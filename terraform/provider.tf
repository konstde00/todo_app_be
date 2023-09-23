terraform {
  backend "remote" {
    hostname     = "app.terraform.io"
    organization = "konstde00"
    workspaces {
      prefix = "todo_app_be"
    }
  }
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.68"
    }
  }
}

provider "aws" {
  region = "us-west-2"
}
