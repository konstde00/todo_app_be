
#######################################
# Cluster
#######################################

resource "aws_ecs_cluster" "cluster" {
  count = var.cluster_name == null ? 1 : 0
  name  = var.name
  tags  = merge(map("Name", var.name), var.tags)
  setting {
    name  = "containerInsights"
    value = "disabled"
  }
}
