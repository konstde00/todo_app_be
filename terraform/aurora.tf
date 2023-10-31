resource "aws_rds_cluster" "todo_app_db_cluster" {
  cluster_identifier = "todo-app-db-cluster"
  engine             = "aurora-mysql"
  engine_mode        = "provisioned"
  database_name      = "todo_app_db"
  master_username    = data.aws_secretsmanager_secret_version.rds_db_user_latest_version.secret_string
  master_password    = data.aws_secretsmanager_secret_version.rds_db_password_latest_version.secret_string

  vpc_security_group_ids  = [aws_security_group.aurora_sg.id]
  preferred_backup_window = "07:00-09:00"

  db_cluster_parameter_group_name = aws_rds_cluster_parameter_group.rds_parameter_group.name

  serverlessv2_scaling_configuration {
    max_capacity = 1.0
    min_capacity = 0.5
  }

  enable_http_endpoint = true

  skip_final_snapshot  = true
  db_subnet_group_name = aws_db_subnet_group.aurora_subnet_group.name

  timeouts {
    create = "1h"
    update = "2h"
    delete = "1h"
  }

  depends_on = [
    aws_rds_cluster_parameter_group.rds_parameter_group
  ]
}

resource "aws_rds_cluster_instance" "todo_app_db_instance" {
  cluster_identifier = aws_rds_cluster.todo_app_db_cluster.id
  instance_class     = "db.serverless"
  engine             = aws_rds_cluster.todo_app_db_cluster.engine
  engine_version     = aws_rds_cluster.todo_app_db_cluster.engine_version

  timeouts {
    create = "1h"
    update = "2h"
    delete = "1h"
  }
}

resource "aws_rds_cluster_parameter_group" "rds_parameter_group" {
  name   = "rds-parameter-group"
  family = "aurora-mysql8.0"

    parameter {
        name  = "binlog_format"
        value = "ROW"
      apply_method = "pending-reboot"
    }

  parameter {
    name  = "binlog_checksum"
    value = "none"
    apply_method = "pending-reboot"
  }

  parameter {
    name  = "binlog_row_image"
    value = "full"
    apply_method = "pending-reboot"
  }
}

resource "aws_security_group" "aurora_sg" {
  name        = "aurora_sg"
  description = "Security group for Aurora DB cluster"
  vpc_id      = aws_vpc.main_vpc.id

  ingress {
    security_groups = [aws_security_group.api_sg.id]
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    Name = "aurora_sg"
  }
}
