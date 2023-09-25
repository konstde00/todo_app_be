module "redis" {
  source = "cloudposse/elasticache-redis/aws"
   version = "0.52.0"
  description = "Redis cluster for the todo-app"

  availability_zones         = ["us-east-1a", "us-east-1b", "us-east-1c"]
  vpc_id                     = module.main_vpc.vpc_id
  allowed_security_group_ids = [aws_security_group.aurora_sg.id]
  subnets                    = module.main_vpc.private_subnets
  cluster_size               = 1
  instance_type              = "cache.t2.micro"
  apply_immediately          = true
  automatic_failover_enabled = false
  engine_version             = "4.0.10"
  family                     = "redis4.0"
  at_rest_encryption_enabled = false
  transit_encryption_enabled = true
  replication_group_id       = "todo-app-redis"

  parameter = [
    {
      name  = "notify-keyspace-events"
      value = "lK"
    }
  ]
}