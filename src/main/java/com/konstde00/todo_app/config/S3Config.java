package com.konstde00.todo_app.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class S3Config {

  @Value("${s3.access-key}")
  String accessKey;

  @Value("${s3.secret-key}")
  String secretKey;

  @Value("${s3.service-endpoint}")
  String serviceEndpoint;

  @Value("${s3.signing-region}")
  String signingRegion;

  @Bean
  @Primary
  public AmazonS3 getCredentials() {

    log.info("getCredentials invoked");
    log.info("accessKey: {}", accessKey);
    log.info("secretKey: {}", secretKey);
    log.info("serviceEndpoint: {}", serviceEndpoint);
    log.info("signingRegion: {}", signingRegion);

    BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

    return AmazonS3ClientBuilder.standard()
        .withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, signingRegion))
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .build();
  }
}
