resource "random_integer" "load_balancer_ext_random_id_suffix" {
  max = 9
  min = 1
  keepers = {
    subnets = join(",", var.vpc_external_subnet_ids)
    name    = coalesce(var.ext_load_balancer_name, "${var.environment_name}-${substr(var.service_name, 0, 8)}")
  }
}

locals {
  ext_lb_random_id = random_integer.load_balancer_ext_random_id_suffix.id
  ext_lb_tg_name   = "${substr(var.environment_name, 0, 7)}-${substr(var.service_name, 0, 8)}-ext-${local.ext_lb_random_id}"

  #Load balancer does not use environment_fancy_name because it has a strict 32-character limit
  ext_lb_name = substr(coalesce(var.ext_load_balancer_name, local.ext_lb_tg_name), 0, 32)
  #Target group does not use environment_fancy_name because then it matches load balancer
  ext_target_group_name = substr(coalesce(var.ext_load_balancer_tg_name, local.ext_lb_tg_name), 0, 32)
}

resource "aws_lb" "ext_load_balancer" {
  for_each = toset(var.provide_external_lb ? ["default"] : [])

  name               = local.ext_lb_name
  internal           = "false"
  load_balancer_type = "application"
  security_groups    = var.external_lb_security_group_ids
  subnets = (length(var.external_lb_subnet_ids) == 0
    ? var.vpc_external_subnet_ids
    : var.external_lb_subnet_ids
  )

  tags = local.ecr_service_tags

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_lb_target_group" "ext_target_group" {
  for_each = toset(var.provide_external_lb ? ["default"] : [])

  name        = local.ext_target_group_name
  port        = var.container_port
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"
  tags        = local.ecr_service_tags

  health_check {
    enabled           = true
    port              = "traffic-port"
    path              = var.health_check_route
    healthy_threshold = var.health_check_healthy_threshold
    #For TCP, healthy threshold and unhealthy thresholds should be the same
    unhealthy_threshold = var.health_check_healthy_threshold
    timeout             = var.health_check_timeout
    interval            = var.health_check_interval
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_lb_listener" "ext_lb_to_tg" {
  for_each = toset(var.provide_external_lb ? ["default"] : [])

  load_balancer_arn = aws_lb.ext_load_balancer["default"].arn
  port              = 80
  protocol          = "HTTP"

  tags = local.ecr_service_tags

  default_action {
    type = "redirect"

    redirect {
      status_code = "HTTP_301"
      protocol    = "HTTPS"
      port        = "443"
    }
  }

  depends_on = [
    aws_lb_listener.ext_lb_to_tg_https,
  ]
}

resource "aws_lb_listener" "ext_lb_to_tg_https" {
  for_each = toset(var.provide_external_lb ? ["default"] : [])

  load_balancer_arn = aws_lb.ext_load_balancer["default"].arn
  port              = 443
  protocol          = "HTTPS"
  certificate_arn   = var.certificate_arn
  ssl_policy        = "ELBSecurityPolicy-TLS-1-2-2017-01"

  tags = local.ecr_service_tags

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ext_target_group["default"].arn
  }

  depends_on = [
    aws_lb_target_group.ext_target_group,
  ]
}
