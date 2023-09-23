resource "aws_rds_cluster" "example" {
  cluster_identifier = "example"
  engine             = "aurora-mysql"
  engine_version     = "5.7.mysql_aurora.2.03.2"
  engine_mode        = "provisioned"
  database_name      = "todo_app_db"
  master_username    = "todo_app"
  master_password    = "must_be_eight_characters"
  availability_zones = [
    "us-east-1a",
    "us-east-1b",
    "us-east-1c"
  ]

  preferred_backup_window = "07:00-09:00"
  db_subnet_group_name    = aws_db_subnet_group.aurora_subnet_group.name

  serverlessv2_scaling_configuration {
    max_capacity = 1.0
    min_capacity = 0.5
  }

  skip_final_snapshot = true

  timeouts {
    create = "1h"
    update = "2h"
    delete = "1h"
  }
}

resource "aws_rds_cluster_instance" "example" {
  cluster_identifier = aws_rds_cluster.example.id
  instance_class     = "db.serverless"
  engine             = aws_rds_cluster.example.engine
  engine_version     = aws_rds_cluster.example.engine_version

  timeouts {
    create = "1h"
    update = "2h"
    delete = "1h"
  }
}