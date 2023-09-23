output "lambda_arn" {
  value = aws_lambda_function.lambda_function.arn
}

output "invoke_arn" {
  value = aws_lambda_function.lambda_function.invoke_arn
}

output "function_name" {
  value = aws_lambda_function.lambda_function.function_name
}

output "lambda_role_arn" {
  value = aws_iam_role.role.arn
}

output "lambda_role_name" {
  value = aws_iam_role.role.name
}