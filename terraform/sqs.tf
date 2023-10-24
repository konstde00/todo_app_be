module "email-verification-sqs" {
  source = "./sqs"

  env = var.environment_name
  max_message_size = 262144
  message_retention_seconds = 300
  name = "${var.environment_name}-todo-app-email-verification-queue"
  tags = var.tags
  visibility_timeout_seconds = 0
}

module "password-reset-sqs" {
  source = "./sqs"

  env = var.environment_name
  max_message_size = 262144
  message_retention_seconds = 300
  name = "${var.environment_name}-todo-app-password-reset-queue"
  tags = var.tags
  visibility_timeout_seconds = 0
}