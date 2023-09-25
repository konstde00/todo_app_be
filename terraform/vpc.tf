module "main_vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "5.1.2"

  name = "main-vpc"
  cidr = "10.0.0.0/16"

  azs             = ["us-east-1a", "us-east-1b", "us-east-1c"]
  private_subnets = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  public_subnets  = ["10.0.101.0/24", "10.0.102.0/24", "10.0.103.0/24"]

  enable_nat_gateway = true
  enable_vpn_gateway = true

  tags = {
    Terraform   = "true"
    Environment = "dev"
  }
}

resource "aws_db_subnet_group" "aurora_subnet_group" {
  name        = "aurora_db_subnet_group"
  description = "Allowed subnets for Aurora DB cluster instances"
  subnet_ids  = module.main_vpc.private_subnets
}

resource "aws_elasticache_subnet_group" "cache_subnet_group" {
  name        = "elasticache-subnet-group"
  description = "Allowed subnets for Cache cluster instances"
  subnet_ids  = module.main_vpc.private_subnets
}