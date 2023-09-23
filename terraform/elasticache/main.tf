data "terraform_remote_state" "shared_network" {
  backend = "remote"
  config = {
    organization = "konstde00"
    workspaces = {
      name = "todo_app_be_${var.environment_name}"
    }
  }
}

resource "aws_security_group" "op_app_cache_cluster_security_group" {
  name        = "${var.environment_name}-app-cache-cluster-security-group"
  description = "Security group for access ElastiCache"
  vpc_id      = data.terraform_remote_state.shared_network.outputs.vpc_primary_vpc_id

  ingress {
    protocol    = "TCP"
    from_port   = 6379
    to_port     = 6379
    self        = true
    description = "TCP traffic to port 6379 (redis)"
  }

  egress {
    cidr_blocks = ["0.0.0.0/0"]
    from_port   = 0
    to_port     = 0
    protocol    = -1
    description = "Allow all outgoing traffic"
  }
}

resource "aws_elasticache_cluster" "cache_cluster" {
  cluster_id           = "${var.environment_name}-cache-cluster"
  engine               = "redis"
  node_type            = "cache.t3.micro"
  num_cache_nodes      = 1
  parameter_group_name = "default.redis6.x"

  engine_version    = "6.0"
  port              = 6379
  subnet_group_name = aws_elasticache_subnet_group.cache_subnet_group.name
  security_group_ids = [
    data.terraform_remote_state.shared_network.outputs.vpc_internal_only_security_group_id,
    aws_security_group.op_app_cache_cluster_security_group.id
  ]
}

resource "aws_elasticache_subnet_group" "cache_subnet_group" {
  name       = "${var.environment_name}-app-cache-subnet"
  subnet_ids = data.terraform_remote_state.shared_network.outputs.vpc_internal_subnet_ids
}
