variable "name" {
  type        = string
  description = "The name of ecs service."
}

variable "cluster_name" {
  type        = string
  description = "The name of ecs cluster."
}

variable "private_subnets" {
  type        = list(string)
  description = "The private subnets associated with the task or service."
}

variable "public_subnets" {
  type        = list(string)
  description = "The public subnets associated with the task or service."
}

variable "vpc_id" {
  type        = string
  description = "VPC Id to associate with ECS Service."
}

variable "desired_count" {
  default     = 0
  type        = string
  description = "The number of instances of the task definition to place and keep running."
}

variable "deployment_maximum_percent" {
  default     = 200
  type        = string
  description = "The upper limit (as a percentage of the service's desiredCount) of the number of running tasks that can be running in a service during a deployment."
}

variable "deployment_minimum_healthy_percent" {
  default     = 100
  type        = string
  description = "The lower limit (as a percentage of the service's desiredCount) of the number of running tasks that must remain running and healthy in a service during a deployment."
}

variable "deployment_controller_type" {
  default     = "ECS"
  type        = string
  description = "Type of deployment controller. Valid values: CODE_DEPLOY, ECS."
}

variable "assign_public_ip" {
  default     = false
  type        = string
  description = "Assign a public IP address to the ENI (Fargate launch type only). Valid values are true or false."
}

variable "ingress_enabled" {
  default     = false
  type        = bool
  description = "Enable Ingress traffic"
}

variable "cpu" {
  default     = "256"
  type        = string
  description = "The number of cpu units used by the task."
}

variable "memory" {
  default     = "512"
  type        = string
  description = "The amount (in MiB) of memory used by the task."
}

variable "requires_compatibilities" {
  default     = "FARGATE"
  type        = string
  description = "A set of launch types required by the task. The valid values are EC2 and FARGATE."
}

variable "iam_path" {
  default     = "/"
  type        = string
  description = "Path in which to create the IAM Role and the IAM Policy."
}

variable "tags" {
  default     = {}
  type        = map(any)
  description = "A mapping of tags to assign to all resources."
}

variable "enabled" {
  default     = true
  type        = bool
  description = "Set to false to prevent the module from creating anything."
}

variable "create_ecs_task_execution_role" {
  default     = true
  type        = string
  description = "Specify true to indicate that ECS Task Execution IAM Role creation."
}

variable "ecs_task_execution_role_arn" {
  default     = ""
  type        = string
  description = "The ARN of the ECS Task Execution IAM Role."
}

variable "cloudwatch_retention_period" {
  default     = 14
  type        = string
  description = "Specifies the number of days you want to retain log events in the specified log group"
}


variable "environment_name" {
  description = "(Required) Either 'staging' or 'production', depending on which environment you want to import"
}

variable "node_opts" {
  description = "(Optional) NODE_OPTS env variable"
  type        = string
  default     = ""
}

variable "image_tag" {
  description = "(Required) The version of the Docker image to base containers off of. Usually starts with 'v.1.XX.feature...'"
}

variable "container_port" {
  description = "(Required) Port that container exposes"
  type        = number
}

variable "service_name" {
  type        = string
  description = "(Required) Name of service"
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

variable "repo_url" {}


variable "container_name" {}

variable "asg_max_cap" {
  default = "4"
}
variable "asg_min_cap" {
  default = "1"
}
variable "cpu_lower_threshold" {
  default = "40"
}
variable "cpu_upper_threshold" {
  default = "60"
}

variable "lb_port" {
  description = "listener port for the loadbalancer"
  default     = "8080"
}

variable "lb_protocol" {
  description = "listener protocol for the loadbalancer"
  default     = "HTTP"
}

variable "tg_port" {
  description = "target group port for the loadbalancer"
  default     = "8080"
}

variable "tg_protocol" {
  description = "target group protocol for the loadbalancer"
  default     = "HTTP"
}