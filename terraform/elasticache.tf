module "this" {
  source = "cloudposse/label/null"
  # Cloud Posse recommends pinning every module to a specific version
  # version = "x.x.x"
  namespace = "var-namespace"
  stage     = "var-stage"
  name      = "var-name"
}

module "redis" {
  source      = "cloudposse/elasticache-redis/aws"
  version     = "0.52.0"
  description = "Redis cluster for the todo-app"

  availability_zones            = ["us-east-1a", "us-east-1b", "us-east-1c"]
  vpc_id                        = aws_vpc.main_vpc.id
  allowed_security_group_ids    = [aws_security_group.aurora_sg.id]
  subnets                       = [aws_subnet.public_subnet1.id, aws_subnet.public_subnet2.id]
  cluster_size                  = 1
  instance_type                 = "cache.t2.micro"
  apply_immediately             = true
  automatic_failover_enabled    = false
  engine_version                = "4.0.10"
  family                        = "redis4.0"
  at_rest_encryption_enabled    = false
  transit_encryption_enabled    = false
  replication_group_id          = "todo-app-redis"
  elasticache_subnet_group_name = aws_elasticache_subnet_group.cache_subnet_group.name
  parameter_group_description   = "Parameter group for the todo-app-redis cluster"

  use_existing_security_groups = true
  associated_security_group_ids = [
    aws_security_group.elasticache_sg.id
  ]

  parameter = [
    {
      name  = "notify-keyspace-events"
      value = "lK"
    }
  ]

  context = module.this.context
}

resource "aws_elasticache_parameter_group" "default" {
  name   = "cache-params"
  family = "redis4.0"
}

resource "aws_security_group" "elasticache_sg" {
  name        = "elasticache_sg"
  description = "Security group for ElastiCache cluster"
  vpc_id      = aws_vpc.main_vpc.id

  ingress {
    from_port   = 0
    to_port     = 65535
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    Name = "elasticache_sg"
  }
}