data "aws_secretsmanager_secret" "rds_db_username" {
  name = "${var.environment_name}/rds_db_username"
}

data "aws_secretsmanager_secret_version" "rds_db_user_latest_version" {
  secret_id = data.aws_secretsmanager_secret.rds_db_username.id
}

data "aws_secretsmanager_secret" "rds_db_password" {
  name = "${var.environment_name}/rds_db_password"
}

data "aws_secretsmanager_secret_version" "rds_db_password_latest_version" {
  secret_id = data.aws_secretsmanager_secret.rds_db_password.id
}