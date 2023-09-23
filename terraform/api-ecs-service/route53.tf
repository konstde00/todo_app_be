locals {
  dns_name = var.dns_name != null ? var.dns_name : var.service_name
}

resource "aws_route53_record" "int_dns" {
  zone_id = data.terraform_remote_state.shared_network.outputs.dns_zone_id
  name    = "${local.dns_name}.${data.aws_route53_zone.internal-zone.name}"
  type    = "A"

  alias {
    name                   = aws_lb.int_load_balancer.dns_name
    zone_id                = aws_lb.int_load_balancer.zone_id
    evaluate_target_health = false
  }
}

/******* External DNS was removed in https://github.com/observepoint/shared-infrastructure/commit/2fbb940ddbfdaacceef1521168059fe8bf07ae2f */
//data "aws_route53_zone" "external-zone" {
//  for_each = toset(var.provide_external_lb ? ["default"] : [])
//  zone_id  = data.terraform_remote_state.shared_network.outputs.dns_zone_id
//}
//
//resource "aws_route53_record" "ext_dns" {
//  for_each = toset(var.provide_external_lb ? ["default"] : [])
//  zone_id  = data.terraform_remote_state.shared_network.outputs.dns_zone_id
//  name     = "${var.dns_name}.${data.aws_route53_zone.external-zone["default"].name}"
//  type     = "A"
//
//  alias {
//    name                   = aws_lb.ext_load_balancer["default"].dns_name
//    zone_id                = aws_lb.ext_load_balancer["default"].zone_id
//    evaluate_target_health = false
//  }
//}
