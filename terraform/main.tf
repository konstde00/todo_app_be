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
  name = "todo-app-be-cluster"

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

module "todo_app_api_service" {

  source        = "./api-ecs-service"
  desired_count = var.api_desired_count
  java_opts     = "-XX:+UseSerialGC -Dspring.profiles.active=${var.spring_profile}"

  memory       = var.api_memory
  cpu          = var.api_cpu
  java_heap_mb = var.api_java_heap_mb

  environment_name = var.environment_name
  image_tag        = var.image_tag
  tags             = var.tags

  vpc_id = aws_vpc.main_vpc.id
  provide_external_lb = true
  security_groups_override = [
    aws_security_group.api_sg.id
    #    data.terraform_remote_state.shared_network.outputs.vpc_internal_only_security_group_id,
    #    data.aws_ssm_parameter.app_cache_cluster_security_group_id.value
  ]

  cluster_id = aws_ecs_cluster.todo_app_be_cluster.id
  container_port                    = 8080
  service_name                      = "todo-app-api"
  ecr_repository_url                = data.aws_ecr_repository.todo_app_be.repository_url
  health_check_route                = "/management/health"
  health_check_grace_period_seconds = 60
  health_check_interval             = 65
  health_check_healthy_threshold    = 4
  health_check_timeout              = 25

  environment_variables = [
    { name : "MYSQL_URL", value: "jdbc:mysql://${aws_rds_cluster.todo_app_db_cluster.endpoint}/todo_app_db" },
    { name : "MYSQL_USER", value: "todo_app" },
    { name : "MYSQL_PASSWORD", value: "must_be_eight_characters" },
    { name : "S3_BUCKET_NAME", value : aws_s3_bucket.todo_app_user_attachments.bucket },
    { name : "REDIS_SERVER", value : "redis://${module.redis.endpoint}:6379" }
  ]

  task_role_arn = aws_iam_role.todo-app-api-role.arn
  execution_role = aws_iam_role.todo-app-api-execution-role.arn
  vpc_external_subnet_ids = [aws_subnet.public_subnet1.id, aws_subnet.public_subnet2.id]
  vpc_internal_subnet_ids = [aws_subnet.public_subnet1.id, aws_subnet.public_subnet2.id]
}

resource "aws_security_group" "api_sg" {
  name        = "api_sg"
  description = "Security group for API"
  vpc_id      = aws_vpc.main_vpc.id

  ingress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    Name = "api_sg"
  }
}