locals {

  var_secrets_map = { for s in var.secrets : s.name => s }

  java_heap_mb = coalesce(var.java_heap_mb, 0.9 * var.memory)

  default_environment_variables = [
    {
      name  = "NEW_RELIC_APP_NAME"
      value = var.service_name
    },
    {
      name  = "JAVA_OPTS"
      value = "-Xmx${var.java_heap_mb}m -Xms${var.java_heap_mb}m ${var.java_opts}"
    },
    {
      name  = "ENVIRONMENT"
      value = title(var.environment_name)
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

resource "aws_ecs_service" "ecs_service" {
  name            = "${var.environment_name}-${var.service_name}"
  cluster         = var.cluster_id
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
    subnets          = concat(var.vpc_external_subnet_ids)
    security_groups  = var.security_groups_override
    assign_public_ip = true
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

resource "aws_ecs_task_definition" "task_definition" {
  family                   = "${var.environment_name}-${var.service_name}-task"
  container_definitions    = data.template_file.container_definition.rendered
  task_role_arn            = var.task_role_arn
  execution_role_arn       = var.execution_role
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  memory                   = var.memory
  cpu                      = var.cpu
  tags                     = local.ecr_service_tags
}

resource "aws_cloudwatch_log_group" "log_group" {
  name = "/ecs/${var.environment_name}-${var.service_name}"

  retention_in_days = 1

  tags = local.ecr_service_tags
}

data "template_file" "container_definition" {
  template = file("${path.module}/container-definition.jsontmpl")

  vars = {
    repo_url = var.ecr_repository_url

    image_tag      = var.image_tag
    container_name = var.service_name

    memory         = var.memory
    cpu            = var.cpu
    container_port = var.container_port

    environment_variables = jsonencode(local.merged_environment_variables)
    secrets               = jsonencode(local.var_secrets_map)

    log_group = aws_cloudwatch_log_group.log_group.name
  }
}
