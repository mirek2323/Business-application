package com.javappa.startappa.startappalight.login.component.email;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.javappa.startappa.startappalight.login.component.email.config.EmailProperties;
import com.javappa.startappa.startappalight.login.component.email.config.EmailResetPasswordProperties;
import com.javappa.startappa.startappalight.login.dto.response.EmailResponseDTO;
import com.javappa.startappa.startappalight.application.domain.User;

@Component
public class EmailSender {

	private static final Logger LOG = LoggerFactory.getLogger(EmailSender.class);

	private JavaMailSender mailSender;
	private EmailProperties emailProperties;
	private EmailResetPasswordProperties emailResetPasswordProperties;

	public EmailSender(JavaMailSender mailSender, EmailProperties emailProperties,
			EmailResetPasswordProperties emailResetPasswordProperties) {
		this.mailSender = mailSender;
		this.emailProperties = emailProperties;
		this.emailResetPasswordProperties = emailResetPasswordProperties;
	}

	public void sendMail(String from, String to, String subject, String body) {

		Instant startTime = Instant.now();
		LOG.info("sendMail - start");

		try {
			MimeMessage message = message(from, to, subject, body);
			mailSender.send(message);
		} catch (MessagingException e) {
			LOG.error(e.getMessage(), e);
		} finally {			
			Instant endTime = Instant.now();
			LOG.info("[METRICS] --------------------> sendMail, time: {} {} ", 
					Duration.between(startTime, endTime).getSeconds(), "sec.");
		}
	}

	private MimeMessage message(String from, String to, String subject, String body) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		message.setSubject(subject);
		message.setContent(body, "text/plain");

		MimeMessageHelper helper = new MimeMessageHelper(message, false);
		helper.setFrom(from);
		helper.setTo(to);
		helper.setSubject(subject);

		return message;
	}

	public EmailResponseDTO sendResetPasswordEmail(User user) {

		String emailBody = MessageFormat.format(emailResetPasswordProperties.getBody(), user.getToken());
		String emailSubject = emailResetPasswordProperties.getSubject();

		sendMail(emailProperties.getFrom(), user.getEmail(), emailSubject, emailBody);

		EmailResponseDTO emailDto = new EmailResponseDTO("sent");

		return emailDto;
	}
}
