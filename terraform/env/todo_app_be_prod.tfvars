environment_name  = "prod"
spring_profile    = "prod"
api_desired_count = 1

apigw_authorizer_id = ""

tags = {
  Budget      = "Production"
  Environment = "prod"
}

apigw_service_name = "todo_app_api"

api_cpu          = 256
api_memory       = 2048
api_java_heap_mb = 1500

certificate_arn = "arn:aws:acm:us-east-1:553440882962:certificate/697d3b2c-1583-4300-a39d-0cde3dd75765"