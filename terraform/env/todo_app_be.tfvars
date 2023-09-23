environment_name            = "staging"
apigateway_environment_name = "staging"
spring_profile              = "staging"
api_desired_count           = 1

apigw_name          = "Staging API Gateway"
apigw_authorizer_id = ""

tags = {
  Budget      = "Development"
  Environment = "Staging"
}

apigw_service_name = "todo_app_api"

api_cpu          = 1024
api_memory       = 2048
api_java_heap_mb = 1500