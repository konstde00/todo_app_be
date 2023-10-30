resource "random_integer" "load_balancer_int_random_id_suffix" {
  max = 1000
  min = 1
  keepers = {
    subnets = join(",", var.vpc_internal_subnet_ids)
    name    = coalesce(var.int_load_balancer_name, "${var.environment_name}-${substr(var.service_name, 0, 15)}")
  }
}

resource "random_string" "target_group_int_random_id_suffix" {
  length  = 3
  special = false
  keepers = {
    timeout  = var.health_check_timeout
    interval = var.health_check_interval
  }
}

locals {
  int_lb_random_id = random_integer.load_balancer_int_random_id_suffix.id
  int_tg_random_id = random_string.target_group_int_random_id_suffix.result
  int_lb_tg_name   = "${substr(var.environment_name, 0, 7)}-${substr(var.service_name, 0, 15)}-int-${local.int_tg_random_id}"

  int_lb_name = substr(coalesce(var.int_load_balancer_name,
  "${substr(var.environment_name, 0, 7)}-${substr(var.service_name, 0, 15)}-int-${local.int_lb_random_id}"), 0, 32)
  int_target_group_name = substr(coalesce(var.int_load_balancer_tg_name, local.int_lb_tg_name), 0, 32)
}

resource "aws_lb" "int_load_balancer" {
  name               = local.int_lb_name
  internal           = "true"
  load_balancer_type = "network"
  subnets            = var.vpc_internal_subnet_ids
  tags               = local.ecr_service_tags

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_lb_target_group" "int_target_group" {
  name        = local.int_target_group_name
  port        = var.container_port
  protocol    = "TCP"
  vpc_id      = var.vpc_id
  target_type = "ip"
  tags        = local.ecr_service_tags

  health_check {
    enabled             = var.health_check_enabled
    port                = var.health_check_port
    path                = var.health_check_route
    healthy_threshold   = var.health_check_healthy_threshold
    unhealthy_threshold = var.health_check_healthy_threshold
    timeout             = var.health_check_timeout
    interval            = var.health_check_interval
  }

  deregistration_delay   = var.deregistration_delay
  connection_termination = var.connection_termination

  stickiness {
    enabled = false
    type    = "source_ip"
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_lb_listener" "int_lb_to_tg" {
  load_balancer_arn = aws_lb.int_load_balancer.arn
  port              = 80
  protocol          = "TCP"

  tags = local.ecr_service_tags

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.int_target_group.arn
  }
}
