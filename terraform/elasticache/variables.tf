variable "environment_name" {
  description = "A short name describing the environment (e. g. staging or prod. environment-specific)"
}

variable "tags" {
  description = "(Required) The tags that should be on every resource, Budget and Environment"
  type = object({
    Budget : string
    Service : string
    Environment : string
  })
}