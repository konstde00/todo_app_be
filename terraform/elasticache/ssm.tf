# Expose information in parameter store
resource "aws_ssm_parameter" "elasticache_cluster_arn" {
  name  = "/op-app/${var.environment_name}/shared/op-app-cache-cluster/arn"
  type  = "String"
  value = aws_elasticache_cluster.cache_cluster.arn
}

resource "aws_ssm_parameter" "elasticache_cluster_id" {
  name  = "/op-app/${var.environment_name}/shared/op-app-cache-cluster/id"
  type  = "String"
  value = aws_elasticache_cluster.cache_cluster.id
}

resource "aws_ssm_parameter" "op_app_cache_cluster_security_group_id" {
  name  = "/op-app/${var.environment_name}/shared/op-app-cache-security-group/id"
  type  = "String"
  value = aws_security_group.cache_cluster_security_group.id
}