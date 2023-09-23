data "terraform_remote_state" "shared_ecs" {
  backend = "remote"
  config = {
    organization = "konstde00"
    workspaces = {
      name = "todo_app_be"
    }
  }
}
data "terraform_remote_state" "shared_ecr" {
  backend = "remote"
  config = {
    organization = "konstde00"
    workspaces = {
      name = "todo_app_be"
    }
  }
}
data "terraform_remote_state" "shared_network" {
  backend = "remote"
  config = {
    organization = "konstde00"
    workspaces = {
      name = "todo_app_be"
    }
  }
}

data "terraform_remote_state" "shared_secrets" {
  backend = "remote"
  config = {
    organization = "ObservePoint"
    workspaces = {
      name = "shared_infrastructure_secrets_${var.environment_name}"
    }
  }
}

data "aws_route53_zone" "internal-zone" {
  zone_id = data.terraform_remote_state.shared_network.outputs.dns_zone_id
}