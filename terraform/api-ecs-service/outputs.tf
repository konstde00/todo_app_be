output "internal_load_balancer_name" {
  value = aws_lb.int_load_balancer.name
}
output "internal_load_balancer_arn" {
  value = aws_lb.int_load_balancer.arn
}
output "internal_load_balancer_id" {
  value = aws_lb.int_load_balancer.id
}
output "internal_load_balancer_dns_name" {
  value = aws_lb.int_load_balancer.dns_name
}
output "internal_load_balancer_zone_id" {
  value = aws_lb.int_load_balancer.zone_id
}

output "internal_target_group_name" {
  value = aws_lb_target_group.int_target_group.name
}
output "internal_target_group_arn" {
  value = aws_lb_target_group.int_target_group.arn
}
output "internal_target_group_id" {
  value = aws_lb_target_group.int_target_group.id
}
output "internal_target_group_port" {
  value = aws_lb_target_group.int_target_group.port
}

output "external_load_balancer_name" {
  value = var.provide_external_lb ? aws_lb.ext_load_balancer["default"].name : ""
}
output "external_load_balancer_arn" {
  value = var.provide_external_lb ? aws_lb.ext_load_balancer["default"].arn : ""
}
output "external_load_balancer_id" {
  value = var.provide_external_lb ? aws_lb.ext_load_balancer["default"].id : ""
}
output "external_load_balancer_dns_name" {
  value = var.provide_external_lb ? aws_lb.ext_load_balancer["default"].dns_name : ""
}
output "external_load_balancer_zone_id" {
  value = var.provide_external_lb ? aws_lb.ext_load_balancer["default"].zone_id : ""
}

output "external_target_group_name" {
  value = var.provide_external_lb ? aws_lb_target_group.ext_target_group["default"].name : ""
}
output "external_target_group_arn" {
  value = var.provide_external_lb ? aws_lb_target_group.ext_target_group["default"].arn : ""
}
output "external_target_group_id" {
  value = var.provide_external_lb ? aws_lb_target_group.ext_target_group["default"].id : ""
}
output "external_target_group_port" {
  value = var.provide_external_lb ? aws_lb_target_group.ext_target_group["default"].port : ""
}

output "internal_route53_record_name" {
  value = aws_route53_record.int_dns.name
}

output "aws_api_gateway_vpc_link_id" {
  value = aws_api_gateway_vpc_link.api-service-vpclink.id
}