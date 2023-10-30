variable "environment_name" {
  description = "A short name describing the environment (e. g. staging or prod. environment-specific)"
}

variable "image_tag" {}

variable "api_desired_count" {
  description = "The number of containers of Reporting API that should be running in this environment"
  type        = number
}

variable "api_cpu" {
  description = "The number of CPU units to reserve for the container"
  type        = number
}

variable "api_memory" {
  description = "The amount of memory (in MiB) to allow the container to use"
  type        = number
}

variable "api_java_heap_mb" {
  description = "The amount of memory (in MB) to allow the JVM to use. Usually it should be 5% less than container memory"
  type        = number
}

variable "spring_profile" {}

variable "certificate_arn" {
  description = "The ARN of the certificate to use for HTTPS"
}

variable "apigw_authorizer_id" {}

variable "apigw_service_name" {}

variable "tags" {
  type = object({
    Budget : string
    Environment : string
  })
}