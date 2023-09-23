#######################################
# Security Group for ECS Service
#######################################

resource "aws_security_group" "default" {
  count       = var.enabled ? 1 : 0
  description = "Security group for ${var.name} ECS cluster"
  name        = local.security_group_name
  vpc_id      = var.vpc_id
  tags        = merge(map("Name", local.security_group_name), var.tags)
}

resource "aws_security_group_rule" "ingress-http" {
  count             = var.ingress_enabled ? 1 : 0
  description       = "${var.name}-ECS-inbound"
  type              = "ingress"
  from_port         = 80
  to_port           = 80
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"] #tfsec:ignore:AWS006
  security_group_id = aws_security_group.default[count.index].id
}

resource "aws_security_group_rule" "ingress-https" {
  count             = var.ingress_enabled ? 1 : 0
  description       = "${var.name}-ECS-inbound"
  type              = "ingress"
  from_port         = 443
  to_port           = 443
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"] #tfsec:ignore:AWS006
  security_group_id = aws_security_group.default[count.index].id
}

resource "aws_security_group_rule" "egress" {
  count             = var.enabled ? 1 : 0
  description       = "${var.name}-ECS-outbound"
  type              = "egress"
  from_port         = 0
  to_port           = 0
  protocol          = "-1"
  cidr_blocks       = ["0.0.0.0/0"] #tfsec:ignore:AWS007
  security_group_id = aws_security_group.default[count.index].id
}
