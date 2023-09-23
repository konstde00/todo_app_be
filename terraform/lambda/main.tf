resource "aws_iam_role" "role" {
  name = "${var.environment_name}-${var.function_name}-role"
  path = "/service-role/"

  assume_role_policy = jsonencode(
    {
      Statement = [
        {
          Action = "sts:AssumeRole"
          Effect = "Allow"
          Principal = {
            Service = "lambda.amazonaws.com"
          }
        },
      ]
      Version = "2012-10-17"
    }
  )

  tags = var.tags
}

resource "aws_iam_role_policy_attachment" "basic_role_policy_attachment" {
  role       = aws_iam_role.role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy_attachment" "vpc_role_policy_attachment" {
  role       = aws_iam_role.role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

locals {
  specifies_subnets         = length(var.subnet_ids) > 0
  specifies_security_groups = length(var.security_group_ids) > 0
  should_specify_vpc        = local.specifies_subnets && local.specifies_security_groups
}


resource "aws_lambda_function" "lambda_function" {
  filename      = var.filename
  function_name = "${var.environment_name}-${var.function_name}"
  handler       = var.add_newrelic_layer ? "newrelic-lambda-wrapper.handler" : var.handler
  role          = aws_iam_role.role.arn

  source_code_hash = filebase64sha256(var.filename)

  runtime = var.runtime
  timeout = var.timeout

  environment {
    variables = var.add_newrelic_layer ? merge(var.environment_variable_configs, local.newrelic_env_variables) : var.environment_variable_configs
  }

  dynamic "vpc_config" {
    for_each = toset(local.should_specify_vpc ? ["default"] : [])

    content {
      subnet_ids         = var.subnet_ids
      security_group_ids = var.security_group_ids
    }
  }

  tags       = var.tags
  layers     = var.add_newrelic_layer ? ["arn:aws:lambda:us-west-2:451483290750:layer:NewRelicNodeJS12X:30"] : []
  depends_on = [aws_iam_role_policy_attachment.basic_role_policy_attachment, aws_iam_role_policy_attachment.vpc_role_policy_attachment]

}
