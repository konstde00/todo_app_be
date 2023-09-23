resource "aws_ecr_repository" "this" {
  name = var.name
  tags = var.tags
}

resource "aws_ecr_lifecycle_policy" "this" {
  repository = aws_ecr_repository.this.name
  policy     = data.template_file.max_image_policy.rendered
}

data "template_file" "max_image_policy" {
  template = file("${path.module}/max_image_lifecycle_policy.jsontmpl")

  vars = {
    image_limit = var.image_limit
  }
}
