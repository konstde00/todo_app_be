package com.konstde00.todo_app.service.password_reset;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konstde00.todo_app.service.dto.PasswordResetMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PasswordResetProducer {

  private final AmazonSQS sqs;
  private final String queueUrl;
  private final ObjectMapper objectMapper;

  public PasswordResetProducer(
      @Value("${aws.sqs.password-reset-queue}") String queueUrl, ObjectMapper objectMapper) {
    this.sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    this.queueUrl = queueUrl;
    this.objectMapper = objectMapper;
  }

  public void sendPasswordResetRequest(PasswordResetMessage message) {
    try {

      String messageBody = objectMapper.writeValueAsString(message);

      SendMessageRequest sendMessageRequest =
          new SendMessageRequest().withQueueUrl(queueUrl).withMessageBody(messageBody);

      sqs.sendMessage(sendMessageRequest);

      log.info("Password reset message sent: {}", message);

    } catch (Exception e) {

      log.error("Error sending password reset message: {}", message, e);
    }
  }
}
