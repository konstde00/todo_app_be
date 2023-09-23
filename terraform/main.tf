data "aws_ecr_repository" "todo_app_be" {
  name = "todo_app_be"
}

module "todo_app_api_service" {
  source        = "./api-ecs-service"
  desired_count = var.api_desired_count
  java_opts     = "-XX:+UseSerialGC -Dnewrelic.environment=${var.spring_profile}-api -Dspring.profiles.active=${var.spring_profile}"

  memory       = var.api_memory
  cpu          = var.api_cpu
  java_heap_mb = var.api_java_heap_mb

  environment_name = var.environment_name
  image_tag        = var.image_tag
  tags             = var.tags

  security_groups_override = [
    #    data.terraform_remote_state.shared_network.outputs.vpc_internal_only_security_group_id,
    #    data.aws_ssm_parameter.op_app_cache_cluster_security_group_id.value
  ]

  container_port                    = 8080
  service_name                      = "todo-app-api"
  ecr_repository_url                = data.aws_ecr_repository.todo_app_be.repository_url
  health_check_route                = "/actuator/health"
  health_check_grace_period_seconds = 60
  health_check_interval             = 65
  health_check_healthy_threshold    = 4
  health_check_timeout              = 25

  environment_variables = [
    { name : "APP_TYPE", value : "api" },
    { name : "S3_BUCKET_URL", value : aws_s3_bucket.todo_app_user_attachments.bucket }
    #    { name : "REDIS_HOST", value : data.aws_elasticache_cluster.op_app_cache_cluster.cache_nodes.0.address },
    #    { name : "REDIS_PORT", value : data.aws_elasticache_cluster.op_app_cache_cluster.cache_nodes.0.port },
  ]

  task_role_arn = aws_iam_role.todo-app-api-role.arn
}