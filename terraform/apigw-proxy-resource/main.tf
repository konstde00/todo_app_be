module "global_variables" {
  source = "../global-variables"
}

resource "aws_api_gateway_resource" "api_resource" {
  parent_id = var.parent_resource_id
  path_part = var.path_part
  rest_api_id = var.rest_api_id
}

resource "aws_api_gateway_method" "api_resource_any" {
  rest_api_id = var.rest_api_id
  resource_id = aws_api_gateway_resource.api_resource.id
  http_method = "ANY"
  authorization = "CUSTOM"
  authorizer_id = var.apigw_authorizer_id
  request_parameters = var.method_request_parameters
}

resource "aws_api_gateway_integration" "api_resource_any_integration" {
  rest_api_id = var.rest_api_id
  resource_id = aws_api_gateway_resource.api_resource.id
  http_method = aws_api_gateway_method.api_resource_any.http_method

  integration_http_method = aws_api_gateway_method.api_resource_any.http_method

  passthrough_behavior = "WHEN_NO_MATCH"

  type = "HTTP_PROXY"
  uri = var.proxy_uri

  connection_id = var.vpc_link_id
  connection_type = "VPC_LINK"

  request_parameters = merge(var.integration_request_parameters, {
    "integration.request.header.usercontext" : "context.authorizer.principalId"
  })
}

resource "aws_api_gateway_method" "api_resource_options" {
  rest_api_id = var.rest_api_id
  resource_id = aws_api_gateway_resource.api_resource.id
  http_method = "OPTIONS"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "api_resource_options_integration" {
  rest_api_id = var.rest_api_id
  resource_id = aws_api_gateway_resource.api_resource.id
  http_method = aws_api_gateway_method.api_resource_options.http_method

  passthrough_behavior = "WHEN_NO_MATCH"

  type = "MOCK"

  connection_type = "INTERNET"

  request_templates = {
    "application/json" = "{\"statusCode\": 200}"
  }
}

resource "aws_api_gateway_method_response" "api_resource_options_response_200" {
  rest_api_id = var.rest_api_id
  resource_id = aws_api_gateway_resource.api_resource.id
  http_method = aws_api_gateway_method.api_resource_options.http_method
  status_code = "200"
  response_models = {
    "application/json" = "Empty"
  }
  response_parameters = module.global_variables.apigw_options_response_parameters
}

resource "aws_api_gateway_integration_response" "api_resource_options_response_200_integration" {
  rest_api_id = var.rest_api_id
  resource_id = aws_api_gateway_resource.api_resource.id
  http_method = aws_api_gateway_method.api_resource_options.http_method
  status_code = aws_api_gateway_method_response.api_resource_options_response_200.status_code
  response_parameters = module.global_variables.apigw_options_integration_response_parameters
  response_templates  = {
    "application/json" = ""
  }

  depends_on = [aws_api_gateway_integration.api_resource_options_integration]
}
