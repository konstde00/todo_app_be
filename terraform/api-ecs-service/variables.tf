variable "environment_name" {
  description = "(Required) Either 'staging' or 'production', depending on which environment you want to import"
}

variable "cluster_id" {
  description = "(Required) The id of ECS cluster"
}

variable "desired_count" {
  description = "(Required) The number of containers to run"
}

variable "memory" {
  description = "(Required) The max limit memory usage for each container in MiB. Valid values: 512, 1024, 2048, and any other multiple of 1024 up to 30720"
}

variable "java_heap_mb" {
  description = "(Optional) The max limit memory usage for the Java heap in MiB (sets Xmx and Xms JVM options). If not provided, defaults to 90% of the memory value."
  type        = number
  default     = null
}

variable "cpu" {
  description = "(Required) The number of 'cpu units' for the service: 256, 1024, 2048, or 4096. At most half of the memory value (see https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-cpu-memory-error.html)"
}

variable "node_opts" {
  description = "(Optional) NODE_OPTS env variable"
  type        = string
  default     = ""
}

variable "java_opts" {
  description = "(Optional) JAVA_OPTS env variable for the ecs service task definition"
  type        = string
  default     = ""
}

variable "image_tag" {
  description = "(Required) The version of the Docker image to base containers off of. Usually starts with 'v.1.XX.feature...'"
}

variable "tags" {
  description = "(Required) Budget monitoring tags"
  type        = map(string)
}

variable "vpc_id" {
  description = "(Required) Port that container exposes"
  type        = string
}

variable "vpc_internal_subnet_ids" {
    description = "(Required) List of internal subnet ids"
    type        = list(string)
}

variable "vpc_external_subnet_ids" {
  description = "(Required) List of external subnet ids"
  type        = list(string)
}

variable "container_port" {
  description = "(Required) Port that container exposes"
  type        = number
}

variable "service_name" {
  type        = string
  description = "(Required) Name of service"
}

variable "task_role_arn" {
  type        = string
  description = "(Optional) If the service should assume a certain role for its permissions, specify the ARN of that role"
  default     = null
}

variable "provide_external_lb" {
  type    = bool
  default = false
}

variable "certificate_arn" {
  description = "ARN of certificate that will be used for HTTPS listener (required if provide_external_lb=true)"
  type        = string
  default     = ""
}

variable "external_lb_security_group_ids" {
  description = "(required if provide_external_lb=true)"
  type        = list(string)
  default     = []
}

variable "external_lb_subnet_ids" {
  description = "(required if provide_external_lb=true)"
  type        = list(string)
  default     = []
}

variable "security_groups_override" {
  description = "(Optional) Override the security groups associated with the service if diverging from standard stage/prod VPC. Will take precedence over network_environment_override"
  type        = list(string)
  default     = null
}

variable "health_check_enabled" {
  description = "(Optional) Whether to enable health checks for the service"
  type        = bool
  default     = true
}

variable "health_check_port" {
  description = "(Optional) Port that health checks will be performed on"
  type        = string
  default     = "traffic-port"
}

variable "health_check_route" {
  description = "Route that will be used for health checks"
  type        = string
  default     = "/"
}

variable "dns_name" {
  description = "DNS name that will be created for internal and external zones"
  type        = string
  default     = null
}

variable "int_load_balancer_name" {
  description = "(Optional) name of the internal load balancer"
  type        = string
  default     = null
}

variable "int_load_balancer_tg_name" {
  description = "(Optional) name of the internal load balancer target group"
  type        = string
  default     = null
}

variable "ext_load_balancer_name" {
  description = "(Optional) name of the external load balancer"
  type        = string
  default     = null
}

variable "ext_load_balancer_tg_name" {
  description = "(Optional) name of the external load balancer target group"
  type        = string
  default     = null
}

variable "ecr_repository_url" {
  description = "(Required) ecr repository url for the ecs service. Will grab one from ../../ecr/outputs.tf if not provided."
  type        = string
}

variable "secrets" {
  description = "(Optional) List of secrets for the running container."
  type = list(object({
    name : string
    valueFrom : string
  }))
  default = []
}

variable "environment_variables" {
  description = "(Optional) List of additional environment variables for the running container."
  type = list(object({
    name : string
    value : string
  }))
  default = []
}

variable "execution_role_override" {
  description = "(Optional) IAM Role ARN for the Agent execution. It is not used in Application Runtime! Use 'task_role_arn' instead."
  type        = string
  default     = ""
}

variable "ecs_platform_version" {
  description = "ecs platform version to use (default = LATEST)"
  type        = string
  default     = "LATEST"
}

variable "health_check_grace_period_seconds" {
  description = "(Optional) time to wait before sending health checks to a container after it starts running"
  type        = number
  default     = 30
}

variable "wait_for_steady_state" {
  description = "(Optional) If true, Terraform will wait for the service to reach a steady state (like aws ecs wait services-stable) before continuing. Default true."
  type        = bool
  default     = true
}

variable "health_check_healthy_threshold" {
  description = "(Optional) Number of consecutive health checks successes required before considering an unhealthy target healthy"
  type        = number
  default     = 2
}

variable "health_check_interval" {
  description = "Approximate amount of time, in seconds, between health checks of an individual target. Minimum value 5 seconds, Maximum value 300 seconds. For lambda target groups, it needs to be greater as the timeout of the underlying lambda. Default 30 seconds"
  type        = number
  default     = 10
}

variable "health_check_timeout" {
  description = "(Optional) Amount of time, in seconds, during which no response means a failed health check. For Application Load Balancers, the range is 2-60 seconds and the default is 5 seconds. For Network Load Balancers, this is 10 seconds for TCP and HTTPS health checks and 6 seconds for HTTP health checks."
  type        = number
  default     = 6
}

variable "deregistration_delay" {
  default     = 30
  description = "(Optional) Amount time for Elastic Load Balancing to wait before changing the state of a deregistering target from draining to unused. The range is 0-3600 seconds."
  type        = number
}
variable "connection_termination" {
  default     = true
  description = "Whether to terminate connections at the end of the deregistration timeout on Network Load Balancers."
  type        = bool
}

variable "newrelic_jar_path" {
  default     = "/"
  description = "absolute path to the newrelic jar in the container"
  type        = string
}