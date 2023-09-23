output "apigw_service_name" {
  value = var.apigw_service_name
}

output "aws_api_gateway_vpc_link_id" {
  value = module.api_v1_tasks.aws_api_gateway_vpc_link_id
}
