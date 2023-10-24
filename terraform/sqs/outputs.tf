output "queue-id" {
  value = aws_sqs_queue.this.id
}

output "queue-name" {
  value = aws_sqs_queue.this.name
}

output "queue-arn" {
  value = aws_sqs_queue.this.arn
}
