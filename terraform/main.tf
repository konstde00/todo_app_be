data "aws_ecr_repository" "todo_app_be" {
  name = "todo_app_be"
}

resource "aws_kms_key" "todo_app_be" {
  description             = "todo_app_be"
  deletion_window_in_days = 7
}

resource "aws_cloudwatch_log_group" "todo_app_be" {
  name = "todo_app_be"
}

resource "aws_ecs_cluster" "todo_app_be_cluster" {
  name = "example"

  configuration {
    execute_command_configuration {
      kms_key_id = aws_kms_key.todo_app_be.arn
      logging    = "OVERRIDE"

      log_configuration {
        cloud_watch_encryption_enabled = true
        cloud_watch_log_group_name     = aws_cloudwatch_log_group.todo_app_be.name
      }
    }
  }
}

#module "todo_app_api_service" {
#
#  source        = "./api-ecs-service"
#  desired_count = var.api_desired_count
#  java_opts     = "-XX:+UseSerialGC -Dnewrelic.environment=${var.spring_profile}-api -Dspring.profiles.active=${var.spring_profile}"
#
#  memory       = var.api_memory
#  cpu          = var.api_cpu
#  java_heap_mb = var.api_java_heap_mb
#
#  environment_name = var.environment_name
#  image_tag        = var.image_tag
#  tags             = var.tags
#
#  vpc_id = module.main_vpc.vpc_id
#  security_groups_override = [
#    #    data.terraform_remote_state.shared_network.outputs.vpc_internal_only_security_group_id,
#    #    data.aws_ssm_parameter.op_app_cache_cluster_security_group_id.value
#  ]
#
#  cluster_id = aws_ecs_cluster.todo_app_be_cluster.id
#  container_port                    = 8080
#  service_name                      = "todo-app-api"
#  ecr_repository_url                = data.aws_ecr_repository.todo_app_be.repository_url
#  health_check_route                = "/actuator/health"
#  health_check_grace_period_seconds = 60
#  health_check_interval             = 65
#  health_check_healthy_threshold    = 4
#  health_check_timeout              = 25
#
#  environment_variables = [
#    { name : "APP_TYPE", value : "api" },
#    { name : "S3_BUCKET_URL", value : aws_s3_bucket.todo_app_user_attachments.bucket }
#    #    { name : "REDIS_HOST", value : data.aws_elasticache_cluster.op_app_cache_cluster.cache_nodes.0.address },
#    #    { name : "REDIS_PORT", value : data.aws_elasticache_cluster.op_app_cache_cluster.cache_nodes.0.port },
#  ]
#
#  task_role_arn = aws_iam_role.todo-app-api-role.arn
#  vpc_external_subnet_ids = [tostring(module.main_vpc.public_subnets[*])]
#  vpc_internal_subnet_ids = [tostring(module.main_vpc.private_subnets[*])]
#}