#module "main_vpc" {
#  source  = "terraform-aws-modules/vpc/aws"
#  version = "5.1.2"
#
#  name = "main-vpc"
#  cidr = "10.0.0.0/16"
#
#  azs             = ["us-east-1a", "us-east-1b", "us-east-1c"]
#  private_subnets = ["10.0.1.0/24"]
#  public_subnets  = ["10.0.101.0/24"]
#
#  enable_nat_gateway = true
#  enable_vpn_gateway = true
#  enable_dns_hostnames = true
#  enable_dns_support = true
#
#  tags = {
#    Terraform   = "true"
#    Environment = "dev"
#  }
#}

# Create internet gateway
resource "aws_internet_gateway" "my_igw" {
  vpc_id = aws_vpc.main_vpc.id
}

resource "aws_vpc" "main_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = "true"
  enable_dns_hostnames = "true"
  instance_tenancy     = "default"
}

# Create public subnets
resource "aws_subnet" "public_subnet1" {
  vpc_id          = aws_vpc.main_vpc.id
  cidr_block      = "10.0.11.0/24" # Adjust CIDR blocks as needed
  availability_zone = "us-east-1a" # Replace with your desired availability zones
  map_public_ip_on_launch = true
}

# Create public subnets
resource "aws_subnet" "public_subnet2" {
  vpc_id          = aws_vpc.main_vpc.id
  cidr_block      = "10.0.12.0/24" # Adjust CIDR blocks as needed
  availability_zone = "us-east-1b" # Replace with your desired availability zones
  map_public_ip_on_launch = true
}

# Create private subnets
resource "aws_subnet" "private_subnet1" {
  vpc_id          = aws_vpc.main_vpc.id
  cidr_block      = "10.0.21.0/24" # Adjust CIDR blocks as needed
  availability_zone = "us-east-1a" # Replace with your desired availability zones
}

# Create a route table for public subnets
resource "aws_route_table" "public_route_table" {
  vpc_id = aws_vpc.main_vpc.id
}

# Create a default route to the internet via the Internet Gateway
resource "aws_route" "public_route" {
  route_table_id         = aws_route_table.public_route_table.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = aws_internet_gateway.my_igw.id
}

# Associate public subnets with the public route table
resource "aws_route_table_association" "public_subnet1_association" {
  subnet_id      = aws_subnet.public_subnet1.id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_route_table_association" "public_subnet2_association" {
  subnet_id      = aws_subnet.public_subnet2.id
  route_table_id = aws_route_table.public_route_table.id
}

resource "aws_db_subnet_group" "aurora_subnet_group" {
  name        = "aurora_db_subnet_group"
  description = "Allowed subnets for Aurora DB cluster instances"
  subnet_ids  = [aws_subnet.public_subnet1.id, aws_subnet.public_subnet2.id]
}

resource "aws_elasticache_subnet_group" "cache_subnet_group" {
  name        = "elasticache-subnet-group"
  description = "Allowed subnets for Cache cluster instances"
  subnet_ids  = [aws_subnet.public_subnet1.id]
}