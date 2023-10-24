variable "create" {
  type = bool
  default = true
}

variable "queue_id" {
  description = "The ID SQS queue that you want to monitor."
  type = string
}

variable "sns_topic_arn" {
  description = "The SNS topic ARN to send alarms to."
  type = string
}

variable "env" {
  description = "The environment."
  type = string
}

variable "messages_not_visible_threshold" {
  description = "The maximum number of not visible messages."
  type = number
  default = 15
}

variable "oldest_message_threshold_seconds" {
  description = "The maximum age of the oldest message."
  type = number
  default = 18000
}

variable "pager_duty_sns_topic_arn" {
  description = "(Optional) The SNS topic to publish alerts to Pager Duty"
  type = string
  default = ""
}

// https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/AlarmThatSendsEmail.html#alarms-and-missing-data
variable "missing_data_treatment" {
  description = "(Optional) The strategy to treat missing data. (missing, ignore, breaching, notBreaching)"
  type = string
  default = "missing"
  validation {
    condition = contains(["missing", "ignore", "breaching", "notBreaching"], var.missing_data_treatment)
    error_message = "Invalid 'missing_data_treatment' strategy. Options: (missing, ignore, breaching, notBreaching)."
  }
}
