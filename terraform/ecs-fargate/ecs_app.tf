#######################################
# Service
#######################################

resource "aws_ecs_service" "api_app" {
  count                              = var.enabled ? 1 : 0
  name                               = var.service_name
  task_definition                    = aws_ecs_task_definition.api_app[count.index].arn
  cluster                            = var.cluster_name == null ? aws_ecs_cluster.cluster[count.index].arn : var.cluster_name
  desired_count                      = var.desired_count
  deployment_maximum_percent         = var.deployment_maximum_percent
  deployment_minimum_healthy_percent = var.deployment_minimum_healthy_percent
  deployment_controller {
    type = var.deployment_controller_type
  }
  network_configuration {
    subnets          = var.private_subnets
    security_groups  = [aws_security_group.default[count.index].id]
    assign_public_ip = var.assign_public_ip
  }
  platform_version = "1.4.0"
  load_balancer {

    target_group_arn = aws_lb_target_group.tg.arn
    container_name   = var.container_name
    container_port   = var.container_port
  }


  launch_type         = "FARGATE"
  scheduling_strategy = "REPLICA"

  lifecycle {
    ignore_changes = [desired_count]
  }
  depends_on = [
    aws_lb_target_group.tg,
    aws_lb.front_end
  ]
}

#######################################
# Task Definition
#######################################

resource "aws_ecs_task_definition" "api_app" {
  count                    = var.enabled ? 1 : 0
  family                   = var.service_name
  execution_role_arn       = var.create_ecs_task_execution_role ? join("", aws_iam_role.default.*.arn) : var.ecs_task_execution_role_arn
  container_definitions    = data.template_file.container_definition.rendered
  cpu                      = var.cpu
  memory                   = var.memory
  requires_compatibilities = [var.requires_compatibilities]
  network_mode             = "awsvpc"
  tags                     = merge(map("Name", var.service_name), var.tags)
}


#######################################
# ECS Task Definitions
#

#######################################

resource "aws_ecs_task_definition" "default" {
  count                    = var.enabled ? 1 : 0
  family                   = var.name
  execution_role_arn       = var.create_ecs_task_execution_role ? join("", aws_iam_role.default.*.arn) : var.ecs_task_execution_role_arn
  container_definitions    = data.template_file.container_definition.rendered
  cpu                      = var.cpu
  memory                   = var.memory
  requires_compatibilities = [var.requires_compatibilities]
  network_mode             = "awsvpc"
  tags                     = merge(map("Name", var.name), var.tags)
}

data "template_file" "container_definition" {
  template = file("${path.module}/container_definitions/op_container_definitions.json")

  vars = {
    repo_url       = var.repo_url ##data.terraform_remote_state.shared_ecr.outputs.ecr_repo_urls[var.service_name]
    image_tag      = var.image_tag
    container_name = var.service_name

    memory = var.memory
    cpu    = var.cpu

    environment    = var.environment_name
    container_port = var.container_port

    node_opts = var.node_opts

    log_group = aws_cloudwatch_log_group.this.name
  }
}


