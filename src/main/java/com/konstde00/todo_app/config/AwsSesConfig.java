package com.konstde00.todo_app.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableAutoConfiguration
public class AwsSesConfig {

  @Value("${aws.region}")
  private String region;

  @Value("${aws.access-key}")
  String accessKey;

  @Value("${aws.secret-key}")
  String secretKey;

  @Bean
  @Primary
  public AmazonSimpleEmailService getAwsSesClient() {
    if (region == null) {
      throw new IllegalStateException("Environment is missing required property - AWS region");
    }

    return AmazonSimpleEmailServiceClientBuilder.standard()
        .withCredentials(
            new AWSStaticCredentialsProvider(
                new com.amazonaws.auth.BasicAWSCredentials(accessKey, secretKey)))
        .withRegion(Regions.US_EAST_1)
        .build();
  }
}
