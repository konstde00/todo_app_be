locals {
  default_secrets = [
    {
      name      = "NEW_RELIC_LICENSE_KEY"
      valueFrom = data.terraform_remote_state.shared_secrets.outputs.new_relic_secret_path
    },
    {
      name      = "SCALYR_API_KEY"
      valueFrom = data.terraform_remote_state.shared_secrets.outputs.scalyr_secret_path
    }
  ]

  default_secrets_map = { for s in local.default_secrets : s.name => s }
  var_secrets_map     = { for s in var.secrets : s.name => s }

  merged_secrets = toset(values(merge(local.default_secrets_map, local.var_secrets_map)))

  java_heap_mb = coalesce(var.java_heap_mb, 0.9 * var.memory)

  default_environment_variables = [
    {
      name  = "NEW_RELIC_APP_NAME"
      value = var.service_name
    },
    {
      name  = "JAVA_OPTS"
      value = "-Xmx${var.java_heap_mb}m -Xms${var.java_heap_mb}m -javaagent:${var.newrelic_jar_path}newrelic.jar ${var.java_opts}"
    },
    {
      name  = "ENVIRONMENT"
      value = title(var.environment_name)
    },
    {
      name  = "NODE_OPTS"
      value = var.node_opts
    }
  ]

  default_env_variables_map = { for e in local.default_environment_variables : e.name => e }
  var_env_variables_map     = { for e in var.environment_variables : e.name => e }

  merged_environment_variables = toset(values(merge(local.default_env_variables_map, local.var_env_variables_map)))

  ecr_service_tags = merge(
    var.tags,
    {
      Service = var.service_name
    }
  )
}

// ================================================
// Service
// ================================================

resource "aws_ecs_service" "ecs_service" {
  name            = "${var.environment_name}-${var.service_name}"
  cluster         = data.terraform_remote_state.shared_ecs.outputs.ecs_cluster_arn
  task_definition = aws_ecs_task_definition.task_definition.arn
  desired_count   = var.desired_count

  health_check_grace_period_seconds = var.health_check_grace_period_seconds

  launch_type      = "FARGATE"
  platform_version = var.ecs_platform_version

  depends_on = [aws_lb_listener.int_lb_to_tg]

  load_balancer {
    target_group_arn = aws_lb_target_group.int_target_group.arn
    container_name   = var.service_name
    container_port   = var.container_port
  }

  dynamic "load_balancer" {
    for_each = toset(var.provide_external_lb ? ["default"] : [])
    content {
      target_group_arn = aws_lb_target_group.ext_target_group["default"].arn
      container_name   = var.service_name
      container_port   = var.container_port
    }
  }

  network_configuration {
    subnets         = data.terraform_remote_state.shared_network.outputs.vpc_internal_subnet_ids
    security_groups = var.security_groups_override != null ? var.security_groups_override : [data.terraform_remote_state.shared_network.outputs.vpc_internal_only_security_group_id]
  }

  deployment_circuit_breaker {
    enable   = false
    rollback = false
  }

  deployment_controller {
    type = "ECS"
  }

  enable_ecs_managed_tags = true
  propagate_tags          = "TASK_DEFINITION"

  tags = merge(
    local.ecr_service_tags,
    {
      Image = var.image_tag
    }
  )
  wait_for_steady_state = var.wait_for_steady_state
}

// ================================================
// Task
// ================================================

resource "aws_ecs_task_definition" "task_definition" {
  family                   = "${var.environment_name}-${var.service_name}-task"
  container_definitions    = data.template_file.container_definition.rendered
  task_role_arn            = var.task_role_arn != null ? var.task_role_arn : ""
  execution_role_arn       = coalesce(var.execution_role_override, data.terraform_remote_state.shared_ecs.outputs.ecs_task_executor_arn)
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  memory                   = var.memory
  cpu                      = var.cpu
  tags                     = local.ecr_service_tags
}

// ================================================
// Logs
// ================================================
resource "aws_cloudwatch_log_group" "log_group" {
  name = "/ecs/${var.environment_name}-${var.service_name}"
  // retention set to 1 day because all logs should go to scalyr
  retention_in_days = 1

  tags = local.ecr_service_tags
}

data "aws_secretsmanager_secret" "scalyr_key" {
  arn = data.terraform_remote_state.shared_secrets.outputs.scalyr_secret_arn
}

data "aws_secretsmanager_secret_version" "current" {
  secret_id = data.aws_secretsmanager_secret.scalyr_key.id
}

data "template_file" "container_definition" {
  template = file("${path.module}/container-definition.jsontmpl")

  vars = {
    repo_url = var.ecr_repository_url != null ? var.ecr_repository_url : data.terraform_remote_state.shared_ecr.outputs.ecr_repo_urls[var.service_name]

    image_tag      = var.image_tag
    container_name = var.service_name

    memory         = var.memory
    cpu            = var.cpu
    container_port = var.container_port

    environment_variables = jsonencode(local.merged_environment_variables)
    secrets               = jsonencode(local.merged_secrets)

    log_group = aws_cloudwatch_log_group.log_group.name

    new_relic_key = data.terraform_remote_state.shared_secrets.outputs.new_relic_secret_path
  }
}
