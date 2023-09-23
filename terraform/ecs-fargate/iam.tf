#######################################
# ECS Task Execution IAM Role
#######################################

resource "aws_iam_role" "default" {
  count = local.enabled_ecs_task_execution

  name               = local.iam_name
  assume_role_policy = data.aws_iam_policy_document.assume_role_policy.json
  path               = var.iam_path
  description        = "${local.iam_description}-role"
  tags               = merge(map("Name", local.iam_name), var.tags)
}

data "aws_iam_policy_document" "assume_role_policy" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_policy" "default" {
  count = local.enabled_ecs_task_execution

  name        = local.iam_name
  policy      = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ecr:GetAuthorizationToken",
                "ecr:BatchCheckLayerAvailability",
                "ecr:GetDownloadUrlForLayer",
                "ecr:BatchGetImage",
                "logs:CreateLogStream",
                "logs:PutLogEvents",
                "kms:Decrypt"
            ],
            "Resource": "*"
        }
    ]
}
EOF
  path        = var.iam_path
  description = "${local.iam_description}-policy"
}

resource "aws_iam_role_policy_attachment" "default" {
  count = local.enabled_ecs_task_execution

  role       = aws_iam_role.default[count.index].name
  policy_arn = aws_iam_policy.default[count.index].arn
}

data "aws_iam_policy" "ecs_task_execution" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}
