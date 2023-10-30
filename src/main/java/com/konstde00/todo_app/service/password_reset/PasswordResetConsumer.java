package com.konstde00.todo_app.service.password_reset;

import com.konstde00.todo_app.service.MailService;
import com.konstde00.todo_app.service.dto.PasswordResetMessage;
import io.awspring.cloud.messaging.listener.Acknowledgment;
import io.awspring.cloud.messaging.listener.Visibility;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PasswordResetConsumer {

  MailService mailService;

  private static final int MESSAGE_VISIBILITY_TIMEOUT_INCREASE_SEC = 120;

  @SqsListener(value = "${aws.sqs.password-reset-queue}")
  public void consumeRequest(
      @Payload PasswordResetMessage message,
      Acknowledgment acknowledgement,
      Visibility visibility) {

    try {

      mailService.sendPasswordResetMail(message.getEmail(), message.getToken());

      acknowledgement.acknowledge();

      log.info("Password reset message processed: {}", message);

    } catch (Exception exception) {

      log.error("Error processing password reset message: {}", message, exception);

      visibility.extend(MESSAGE_VISIBILITY_TIMEOUT_INCREASE_SEC);
    }
  }
}
