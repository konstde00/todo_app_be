resource "aws_rds_cluster" "todo_app_be" {
  cluster_identifier = "${var.environment_name}-todo-app-be"
  engine             = "aurora-postgresql"
  engine_mode        = "provisioned"
  engine_version     = "13.6"
  database_name      = "test"
  master_username    = "test"
  master_password    = "must_be_eight_characters"

  serverlessv2_scaling_configuration {
    max_capacity = 1.0
    min_capacity = 0.5
  }
}

resource "aws_rds_cluster_instance" "todo_app_be" {
  cluster_identifier = aws_rds_cluster.todo_app_be.id
  instance_class     = "db.serverless"
  engine             = aws_rds_cluster.todo_app_be.engine
  engine_version     = aws_rds_cluster.todo_app_be.engine_version
}