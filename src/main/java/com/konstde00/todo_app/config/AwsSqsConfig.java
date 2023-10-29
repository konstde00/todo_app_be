package com.konstde00.todo_app.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AwsSqsConfig {

  @Value("${aws.region}")
  private String region;

  @Primary
  @Bean
  public AmazonSQSAsync amazonSQSAsync(@Value("${cloud.aws.sqs.endpoint:}") String sqsEndpoint) {
    if (region == null) {
      List<String> missingProperties = new ArrayList<>();
      missingProperties.add("region");

      throw new IllegalStateException(
          "Environment is missing required properties" + String.join(",", missingProperties));
    }

    var clientBuilder = AmazonSQSAsyncClientBuilder.standard();

    if (StringUtils.isEmpty(sqsEndpoint)) {
      return clientBuilder.withRegion(region).build();
    } else {
      return clientBuilder
          .withEndpointConfiguration(
              new AwsClientBuilder.EndpointConfiguration(sqsEndpoint, region))
          .build();
    }
  }
}
