environment_name  = "staging"
spring_profile    = "dev"
api_desired_count = 1

apigw_authorizer_id = ""

tags = {
  Budget      = "Staging"
  Environment = "staging"
}

apigw_service_name = "todo_app_api"

api_cpu          = 256
api_memory       = 2048
api_java_heap_mb = 1500

certificate_arn = "arn:aws:acm:us-east-1:553440882962:certificate/484c78d9-5a0d-4145-9731-8ddfe7f0816f"