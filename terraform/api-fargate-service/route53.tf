locals {
  dns_name = var.dns_name != null ? var.dns_name : var.service_name
}
