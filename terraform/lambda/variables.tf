variable "environment_name" {
  description = "(Required) Short name of the development environment. (ex. dev, stage, prod)"
}

variable "filename" {
  description = "(Required) Path to the bundled code for the lambda function. (ex './lambda.zip')"
}

variable "function_name" {
  description = "(Required) Name for the aws lambda function"
}

variable "handler" {
  description = "(Required) function that AWS should call when invoking the lambda function. (ex 'index.handler', used in a javascript lambda when index.js exports a function called 'handler')"
}

variable "runtime" {
  description = "(Required) Labmda runtime.  (ex nodejs12.x, python3.7)"
}

variable "tags" {
  description = "(Optional) tags that will be applied to the aws lambda function and the iam role"
  default     = {}
}

variable "timeout" {
  description = "(Optional) how many seconds should lambda wait before timing out"
  default     = 3
}

variable "environment_variable_configs" {
  description = "Object containing environment variables"
  type        = any
  default     = null
}

variable "subnet_ids" {
  description = "(Optional) An array of subnets to attach lambda executions to"
  default     = []
}

variable "security_group_ids" {
  description = "(Optional) An array of security groups for lambda to execute under"
  default     = []
}
