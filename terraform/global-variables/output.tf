
output "apigw_options_response_parameters" {
  value = {
    "method.response.header.Access-Control-Allow-Headers" = false
    "method.response.header.Access-Control-Allow-Methods" = false
    "method.response.header.Access-Control-Allow-Origin"  = false
    "method.response.header.Access-Control-Max-Age"       = false
  }
}

output "apigw_options_integration_response_parameters" {
  value = {
    "method.response.header.Access-Control-Allow-Headers" = "'Content-Type,Authorization,X-Amz-Date,X-Api-Key,X-Amz-Security-Token'"
    "method.response.header.Access-Control-Allow-Methods" = "'DELETE,GET,HEAD,OPTIONS,PATCH,POST,PUT'"
    "method.response.header.Access-Control-Max-Age"       = "'3600'" //For browsers to cache OPTIONS request in seconds = 1h
  }
}