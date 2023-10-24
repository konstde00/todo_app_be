locals {
  dlq_redrive_policy = jsonencode({
    deadLetterTargetArn       = aws_sqs_queue.this-dlq.arn
    maxReceiveCount           = var.dlq_redrive_policy_maxReceiveCount
  })
}


resource aws_sqs_queue this {
  name                              = var.name
  fifo_queue                        = var.fifo_queue
  visibility_timeout_seconds        = var.visibility_timeout_seconds
  message_retention_seconds         = var.message_retention_seconds
  max_message_size                  = var.max_message_size
  delay_seconds                     = var.delay_seconds
  receive_wait_time_seconds         = var.receive_wait_time_seconds
  redrive_policy                    = var.create_dlq ? local.dlq_redrive_policy : null
  content_based_deduplication       = var.content_based_deduplication
  tags                              = var.tags
}

resource aws_sqs_queue this-dlq {
  name                              = "${var.name}-dlq"
  fifo_queue                        = var.fifo_queue
  visibility_timeout_seconds        = var.visibility_timeout_seconds
  message_retention_seconds         = var.message_retention_seconds
  max_message_size                  = var.max_message_size
  delay_seconds                     = var.delay_seconds
  receive_wait_time_seconds         = var.receive_wait_time_seconds
  redrive_policy                    = null
  content_based_deduplication       = var.content_based_deduplication
  tags                              = var.tags
}

module "queue_alerts" {
  source                              = "./../sqs-alarms"
  create                              = var.create_alert
  env                                 = var.env
  queue_id                            = aws_sqs_queue.this.name
  sns_topic_arn                       = var.alerts_sns_topic_arn
  messages_not_visible_threshold      = var.queue_messages_not_visible_threshold_count
  oldest_message_threshold_seconds    = var.queue_oldest_message_threshold_seconds
  missing_data_treatment              = var.missing_data_treatment
  pager_duty_sns_topic_arn            = var.pager_duty_sns_topic_arn
}

module "dlq_alerts" {
  source                              = "./../sqs-alarms"
  create                              = var.create_alert && var.create_dlq
  env                                 = var.env
  queue_id                            = aws_sqs_queue.this-dlq.name
  sns_topic_arn                       = var.alerts_sns_topic_arn
  messages_not_visible_threshold      = var.dlq_messages_not_visible_threshold_count
  oldest_message_threshold_seconds    = var.dlq_oldest_message_threshold_seconds
  missing_data_treatment              = var.missing_data_treatment_dlq
}
