#######################################
# Application Load Balancer
#######################################

resource "aws_lb" "front_end" {
  count              = var.enabled ? 1 : 0
  name_prefix        = "lb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.default[count.index].id]
  subnets            = var.public_subnets

  enable_deletion_protection = false

  tags = {
    Environment = "production"
  }
}

resource "aws_lb_listener" "front_end" {
  count             = var.enabled ? 1 : 0
  load_balancer_arn = aws_lb.front_end[count.index].arn
  port              = var.lb_port
  protocol          = var.lb_protocol

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.tg.arn
  }
}

#######################################
# Target Group
#######################################

resource "aws_lb_target_group" "tg" {
  name_prefix = "tg"
  port        = var.tg_port
  protocol    = var.tg_protocol
  vpc_id      = var.vpc_id
  target_type = "ip"
  depends_on = [
    aws_lb.front_end
  ]
}
