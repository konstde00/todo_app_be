resource "aws_iam_role" "todo-app-api-role" {
  name               = "${var.environment_name}-todo-app-api-task-role"
  tags               = var.tags
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_policy" "todo-app-api-access-policy" {
  name = "${var.environment_name}-todo-app-api-access-policy"
  path = "/"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "s3:GetObject"
        ],
        Resource = "arn:aws:s3:::*/*"
      },
      {
        Effect = "Allow",
        Action = [
          "s3:ListBucket",
          "s3:PutObject"
        ],
        Resource = "arn:aws:s3:::*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "todo-app-api-role-policy-attachment" {
  policy_arn = aws_iam_policy.todo-app-api-access-policy.arn
  role       = aws_iam_role.todo-app-api-role.name
}



resource "aws_iam_role" "todo-app-api-execution-role" {
  name               = "${var.environment_name}-todo-app-api-execution-role"
  tags               = var.tags
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_policy" "todo-app-api-execution-access-policy" {
  name = "${var.environment_name}-todo-app-api-execution-access-policy"
  path = "/"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "*"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "todo-app-api-execution-role-policy-attachment" {
  policy_arn = aws_iam_policy.todo-app-api-execution-access-policy.arn
  role       = aws_iam_role.todo-app-api-execution-role.name
}