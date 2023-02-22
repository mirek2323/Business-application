package com.javappa.startappa.startappalight.login.component.email.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

	private EmailProperties mailProperties;

	@Autowired
	public MailConfig(EmailProperties mailProperties) {
		this.mailProperties = mailProperties;
	}

	@Bean
	public JavaMailSender javaMailService() {

		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(mailProperties.getHost());
		javaMailSender.setPort(mailProperties.getPort());
		javaMailSender.setPassword(mailProperties.getPassword());
		javaMailSender.setUsername(mailProperties.getFrom());
		javaMailSender.setJavaMailProperties(getMailProperties());

		return javaMailSender;
	}

	private Properties getMailProperties() {

		Properties properties = new Properties();
		properties.setProperty("mail.transport.protocol", mailProperties.getProtocol());
		properties.setProperty("mail.smtp.auth", "true");
		properties.setProperty("mail.smtp.ssl.enable", "true");
		properties.setProperty("mail.debug", mailProperties.getDebug());
		properties.setProperty("mail.smtps.ssl.checkserveridentity", "true");
		properties.setProperty("mail.smtps.ssl.trust", "*");

		return properties;
	}
}