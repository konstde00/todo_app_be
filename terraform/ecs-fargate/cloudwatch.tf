#######################################
# API CloudWatch Log group
#######################################

resource "aws_cloudwatch_log_group" "ecs_api" {

  name_prefix = "ecs/${var.container_name}"

  tags = {
    Environment = terraform.workspace
    Application = var.container_name
  }
}

#######################################
# Default CloudWatch Log Group
#######################################

resource "aws_cloudwatch_log_group" "this" {
  name_prefix       = "ecs/${var.name}"
  tags              = merge(map("Name", var.name), var.tags)
  retention_in_days = 14

}

#######################################
# CloudWatch Alarms
#######################################

resource "aws_cloudwatch_metric_alarm" "cloudwatch_metric_alarm_rpm_high" {
  count             = var.enabled ? 1 : 0
  alarm_name        = "${var.container_name}-CPU-High"
  alarm_description = "Managed by Terraform"
  alarm_actions     = ["${aws_appautoscaling_policy.appautoscaling_policy_rpm_scale_up[count.index].arn}"]
  #ok_actions          = ["${var.alarm_pagerduty_sns}"]
  comparison_operator = "GreaterThanOrEqualToThreshold"
  evaluation_periods  = "1"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  period              = "300"
  statistic           = "Sum"
  threshold           = var.cpu_upper_threshold

  dimensions = {
    ClusterName = aws_ecs_cluster.cluster[count.index].name
    ServiceName = aws_ecs_service.api_app[count.index].name
  }
}

resource "aws_cloudwatch_metric_alarm" "cloudwatch_metric_alarm_rpm_low" {
  count             = var.enabled ? 1 : 0
  alarm_name        = "${var.container_name}-CPU-low"
  alarm_description = "Managed by Terraform"
  alarm_actions     = ["${aws_appautoscaling_policy.appautoscaling_policy_rpm_scale_down[count.index].arn}"]
  #ok_actions          = ["${var.alarm_pagerduty_sns}"]
  comparison_operator = "LessThanOrEqualToThreshold"
  evaluation_periods  = "1"
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  period              = "300"
  statistic           = "Sum"
  threshold           = var.cpu_lower_threshold

  dimensions = {
    ClusterName = aws_ecs_cluster.cluster[count.index].name
    ServiceName = aws_ecs_service.api_app[count.index].name
  }
  depends_on = [aws_appautoscaling_policy.appautoscaling_policy_rpm_scale_down]
}


###container alarm test, reserve cpu low, trigers scale up
/*
resource "aws_cloudwatch_metric_alarm" "cloudwatch_metric_alarm_rpm_low" {
  count               = var.enabled ? 1 : 0
  alarm_name          = "${var.container_name}-CPU-reserve-low"
  alarm_description   = "Managed by Terraform"
  alarm_actions       = ["${aws_appautoscaling_policy.appautoscaling_policy_rpm_scale_up[count.index].arn}"]
  #ok_actions          = ["${var.alarm_pagerduty_sns}"]
  comparison_operator = "LessThanOrEqualToThreshold"
  evaluation_periods  = "1"
  metric_name         = "CpuReserved"
  namespace           = "ECS/ContainerInsights"
  period              = "300"
  statistic           = "Sum"
  threshold           = "50"

  dimensions = {
    ClusterName = aws_ecs_cluster.cluster[count.index].name
    ServiceName  = aws_ecs_service.api_app[count.index].name
    TaskDefinitionFamily = ""
  }
  depends_on = [aws_appautoscaling_policy.appautoscaling_policy_rpm_scale_up]
}
*/