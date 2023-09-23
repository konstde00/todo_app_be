#######################################
# Data and Locals
#######################################

# Get AWS account ID
data "aws_caller_identity" "current" {}

# Get current region
data "aws_region" "current" {}

locals {
  region                     = data.aws_region.current.name
  account_id                 = data.aws_caller_identity.current.account_id
  security_group_name        = "${var.name}-ecs-fargate"
  iam_description            = "${var.name}-executor"
  iam_name                   = "${var.name}-ecs-task-execution"
  enabled_ecs_task_execution = var.enabled && var.create_ecs_task_execution_role ? 1 : 0
}