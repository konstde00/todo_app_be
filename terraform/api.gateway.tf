resource "aws_api_gateway_rest_api" "todo_app_api_v1" {
  name        = "TodoAppApiV1"
  description = "Todo App API V1"
}

resource "aws_api_gateway_resource" "todo_app_api_v1" {
  rest_api_id = aws_api_gateway_rest_api.todo_app_api_v1.id
  parent_id   = aws_api_gateway_rest_api.todo_app_api_v1.root_resource_id
  path_part   = "v1"
}

module "api_v3_audit_pages" {
  source                         = "./apigw-proxy-resource"
  parent_resource_id             = aws_api_gateway_resource.todo_app_api_v1.id
  path_part                      = "pages"
  rest_api_id                    = aws_api_gateway_rest_api.todo_app_api_v1.id
  apigw_authorizer_id            = var.apigw_authorizer_id
  vpc_link_id                    = "$${stageVariables.${var.apigw_service_name}_vpc_link_id}"
  proxy_uri                      = "http://${module.todo_app_api_service.internal_load_balancer_dns_name}/v1/tasks"
  method_request_parameters      = {}
  integration_request_parameters = {}
}