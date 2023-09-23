variable "parent_resource_id" {
  description = "ID of the parent resource to create our new resource under (ex. id for `/v3`)"
}

variable "path_part" {
  description = "path part of the resource to be created (ex. 'navigation-items')"
}

variable "rest_api_id" {
  description = "ID of the rest api that the resource will be created in"
}

variable "apigw_authorizer_id" {}

variable "vpc_link_id" {
  description = "ID of the vpc link used to "
}

variable "proxy_uri" {
  description = "endpoint url when calling the passing through to the proxy resource (ex. http://staging-data-collection-api-nlb-0ce72fb64df3666c.elb.us-west-2.amazonaws.com/v3/action-sets)"
}

variable "method_request_parameters" {
  description = "(Optional) A map of request parameters (from the path, query string and headers) that should be passed to the integration."
  default = {}
}

variable "integration_request_parameters" {
  description = "(Optional) A list of cache key parameters for the integration"
  default = {}
}
