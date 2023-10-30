variable "name" {
  type        = string
  description = "(Required) The name of the AWS queue. Should be unique across the account. The DLQ queue will have '-dlq' prefix appended."
}

variable "fifo_queue" {
  default     = false
  description = "(Optional) Set the queue type to FIFO."
}

variable "visibility_timeout_seconds" {
  type        = number
  description = "(Required) The SQS message visibility timeout."
}

variable "message_retention_seconds" {
  type        = number
  description = "(Required) The max SQS message age."
}

variable "max_message_size" {
  type        = number
  description = "(Required) The max SQS message size."
}

variable "delay_seconds" {
  type        = number
  default     = 0
  description = "(Optional) Set a delay for the message consumption."
}

variable "receive_wait_time_seconds" {
  type        = number
  default     = 20
  description = "(Optional) Set the polling time."

}

variable "content_based_deduplication" {
  default     = false
  type        = bool
  description = "(Optional) Deduplicate the messages based on content."
}

variable "create_dlq" {
  default     = true
  type        = bool
  description = "(Optional) Create a redrive policy with the DLQ strategy."
}

variable "tags" {
  type = map(string)
}

variable "create_alert" {
  description = "(Optional) Create alerts for both declared queue and dlq."
  type        = bool
  default     = false
}

variable "env" {
  description = "(Optional) Environment mark for alert name"
  type        = string
  default     = ""
}

variable "alerts_sns_topic_arn" {
  description = "(Optional) The SNS topic to publish alerts to."
  type        = string
  default     = ""
}

variable "pager_duty_sns_topic_arn" {
  description = "(Optional) The SNS topic to publish alerts to Pager Duty"
  type        = string
  default     = ""
}

variable "queue_messages_not_visible_threshold_count" {
  type    = number
  default = null
}

variable "queue_oldest_message_threshold_seconds" {
  type    = number
  default = 600
}

variable "dlq_messages_not_visible_threshold_count" {
  type    = number
  default = 10
}

variable "dlq_oldest_message_threshold_seconds" {
  type    = number
  default = 600
}

variable "missing_data_treatment" {
  description = "(Optional) The strategy to treat missing data. (missing, ignore, breaching, notBreaching)"
  type        = string
  default     = "missing"
}

variable "missing_data_treatment_dlq" {
  description = "(Optional) The strategy to treat missing data. (missing, ignore, breaching, notBreaching)"
  type        = string
  default     = "ignore"
}

variable "dlq_redrive_policy_maxReceiveCount" {
  description = "(Optional) The max same message receive count for dead-letter queues"
  type        = number
  default     = 10
}
