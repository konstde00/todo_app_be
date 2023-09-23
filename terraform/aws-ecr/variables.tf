variable "name" {
  description = "The name of the repository."
  type        = string
}

variable "tags" {
  description = "The repository tags."
  type        = map(string)
}

variable "image_limit" {
  description = "Maximum number of untagged images (not aws-staging or aws-prod) to keep in repo"
  default     = 20
}
