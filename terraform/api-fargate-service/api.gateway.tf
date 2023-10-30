resource "aws_api_gateway_vpc_link" "api-service-vpclink" {
  name        = "${var.environment_name}-${var.service_name}-vpclink"
  target_arns = [aws_lb.int_load_balancer.arn]
  tags        = var.tags
}