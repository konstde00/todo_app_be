#######################################
# AutoScaling Target
#######################################

resource "aws_appautoscaling_target" "ecs_target" {
  count              = var.enabled ? 1 : 0
  max_capacity       = var.asg_max_cap
  min_capacity       = var.asg_min_cap
  resource_id        = "service/${aws_ecs_cluster.cluster[count.index].name}/${aws_ecs_service.api_app[count.index].name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}

#######################################
# AutoScaling Policies
#######################################

resource "aws_appautoscaling_policy" "appautoscaling_policy_rpm_scale_down" {
  count              = var.enabled ? 1 : 0
  name               = "${var.container_name}-scale-down"
  policy_type        = "StepScaling"
  resource_id        = aws_appautoscaling_target.ecs_target[count.index].resource_id
  scalable_dimension = aws_appautoscaling_target.ecs_target[count.index].scalable_dimension
  service_namespace  = aws_appautoscaling_target.ecs_target[count.index].service_namespace

  step_scaling_policy_configuration {
    adjustment_type         = "ChangeInCapacity"
    cooldown                = 60
    metric_aggregation_type = "Average"

    step_adjustment {
      metric_interval_upper_bound = 0
      scaling_adjustment          = "-1"
    }
  }

  depends_on = [aws_appautoscaling_target.ecs_target]
}


resource "aws_appautoscaling_policy" "appautoscaling_policy_rpm_scale_up" {
  count              = var.enabled ? 1 : 0
  name               = "${var.container_name}-scale-up"
  policy_type        = "StepScaling"
  resource_id        = aws_appautoscaling_target.ecs_target[count.index].resource_id
  scalable_dimension = aws_appautoscaling_target.ecs_target[count.index].scalable_dimension
  service_namespace  = aws_appautoscaling_target.ecs_target[count.index].service_namespace

  step_scaling_policy_configuration {
    adjustment_type         = "ChangeInCapacity"
    cooldown                = "60"
    metric_aggregation_type = "Average"

    step_adjustment {
      metric_interval_lower_bound = "5"
      scaling_adjustment          = "1"
    }
  }

  depends_on = [aws_appautoscaling_target.ecs_target]
}
