output "resource_id" {
  value = aws_api_gateway_resource.api_resource.id
}

output "deployment_trigger" {
  value = sha1(jsonencode([
    aws_api_gateway_resource.api_resource,
    aws_api_gateway_method.api_resource_any,
    aws_api_gateway_integration.api_resource_any_integration,
    aws_api_gateway_method.api_resource_options,
    aws_api_gateway_integration.api_resource_options_integration,
    aws_api_gateway_method_response.api_resource_options_response_200,
    aws_api_gateway_integration_response.api_resource_options_response_200_integration,
    aws_api_gateway_resource.api_resource_proxy,
    aws_api_gateway_method.api_resource_proxy_any,
    aws_api_gateway_integration.api_resource_proxy_any_integration,
    aws_api_gateway_method.api_resource_proxy_options,
    aws_api_gateway_integration.api_resource_proxy_options_integration,
    aws_api_gateway_method_response.api_resource_proxy_options_response_200,
    aws_api_gateway_integration_response.api_resource_proxy_options_response_200_integration
  ]))
}
