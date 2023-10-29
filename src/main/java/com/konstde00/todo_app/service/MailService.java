package com.konstde00.todo_app.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.konstde00.todo_app.domain.User;
import java.util.List;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tech.jhipster.config.JHipsterProperties;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailService {

  private static final String USER = "user";

  private static final String BASE_URL = "baseUrl";

  JHipsterProperties jHipsterProperties;

  JavaMailSender javaMailSender;

  MessageSource messageSource;

  SpringTemplateEngine templateEngine;

  String fromEmailAddress;
  AmazonSimpleEmailService sesClient;

  public MailService(
      JHipsterProperties jHipsterProperties,
      JavaMailSender javaMailSender,
      MessageSource messageSource,
      SpringTemplateEngine templateEngine,
      AmazonSimpleEmailService sesClient,
      @Value("${email-sender-address}") String fromEmailAddress) {
    this.sesClient = sesClient;
    this.messageSource = messageSource;
    this.templateEngine = templateEngine;
    this.javaMailSender = javaMailSender;
    this.fromEmailAddress = fromEmailAddress;
    this.jHipsterProperties = jHipsterProperties;
  }

  @Async
  public void sendEmail(String to, String subject, String content) {

    try {

      final SendEmailRequest request = createSendEmailRequest(List.of(to), subject, content);

      try {
        sesClient.sendEmail(request);
      } catch (Exception e) {

        log.error(
            "Failed to send email, subject {}, message {}, toAddresses: {}",
            subject,
            content,
            to,
            e);

        throw e;
      }
      log.debug("Sent email to User '{}'", to);
    } catch (MailException e) {
      log.warn("Email could not be sent to user '{}'", to, e);
    }
  }

  @Async
  public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
    if (user.getEmail() == null) {
      log.debug("Email doesn't exist for user '{}'", user.getLogin());
      return;
    }
    Locale locale = Locale.forLanguageTag(user.getLangKey());
    Context context = new Context(locale);
    context.setVariable(USER, user);
    context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
    String content = templateEngine.process(templateName, context);
    String subject = messageSource.getMessage(titleKey, null, locale);
    sendEmail(user.getEmail(), subject, content);

    final SendEmailRequest request =
        createSendEmailRequest(List.of(user.getEmail()), subject, content);

    try {
      sesClient.sendEmail(request);
    } catch (Exception e) {

      log.error(
          "Failed to send email, subject {}, message {}, toAddresses: {}",
          subject,
          content,
          user.getEmail(),
          e);

      throw e;
    }
  }

  @Async
  public void sendActivationEmail(User user) {
    log.debug("Sending activation email to '{}'", user.getEmail());
    sendEmailFromTemplate(user, "mail/activationEmail", "email.activation.title");
  }

  @Async
  public void sendCreationEmail(User user) {
    log.debug("Sending creation email to '{}'", user.getEmail());
    sendEmailFromTemplate(user, "mail/creationEmail", "email.activation.title");
  }

  @Async
  public void sendPasswordResetMail(String email, String token) {

    String subject = "Recovery code";
    String message = "Your recovery code is " + token;

    sendEmail(email, subject, message);
  }

  protected SendEmailRequest createSendEmailRequest(
      List<String> toAddresses, String subject, String content) {
    return new SendEmailRequest()
        .withDestination(new Destination().withToAddresses(toAddresses))
        .withMessage(
            new Message()
                .withBody(new Body().withText(new Content().withCharset("UTF-8").withData(content)))
                .withSubject(new Content().withCharset("UTF-8").withData(subject)))
        .withSource(fromEmailAddress);
  }
}
