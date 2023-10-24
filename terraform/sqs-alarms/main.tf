locals {
  thresholds = {
    MessagesNotVisibleThreshold = var.messages_not_visible_threshold
    OldestMessageAgeSecondsThreshold = var.oldest_message_threshold_seconds
  }

  default_period = 600

  pager_duty_arn = var.pager_duty_sns_topic_arn != "" ? [var.pager_duty_sns_topic_arn] : []
  alarm_actions = concat([var.sns_topic_arn], local.pager_duty_arn)
  ok_actions = concat([var.sns_topic_arn], local.pager_duty_arn)
}

resource "aws_cloudwatch_metric_alarm" "too_many_not_visible_messages" {
  count = var.create ? 1 : 0

  alarm_name = "SQS :: ${var.env} :: ${var.queue_id} :: too_many_not_visible_messages"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods = "1"
  metric_name = "ApproximateNumberOfMessagesNotVisible"
  namespace = "AWS/SQS"
  period = local.default_period
  statistic = "Average"
  threshold = local.thresholds["MessagesNotVisibleThreshold"]
  treat_missing_data = var.missing_data_treatment
  alarm_description = "Number of messages not visible over last 10 minutes too high"
  alarm_actions = [
    var.sns_topic_arn]
  ok_actions = [
    var.sns_topic_arn]

  dimensions = {
    QueueName = var.queue_id
  }
}

resource "aws_cloudwatch_metric_alarm" "too_old_message" {
  count = var.create ? 1 : 0

  alarm_name = "SQS :: ${var.env} :: ${var.queue_id} :: too_old_message"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods = "1"
  metric_name = "ApproximateAgeOfOldestMessage"
  namespace = "AWS/SQS"
  period = local.default_period
  statistic = "Average"
  threshold = local.thresholds["OldestMessageAgeSecondsThreshold"]
  treat_missing_data = var.missing_data_treatment
  alarm_description = "Age of the oldest message in queue is too high"
  alarm_actions = local.alarm_actions
  ok_actions = local.ok_actions

  dimensions = {
    QueueName = var.queue_id
  }
}
