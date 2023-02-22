package com.javappa.startappa.startappalight.login.component.security;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.javappa.startappa.startappalight.login.repository.UserRepository;

@Component
public class TokenRemovalScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(TokenRemovalScheduler.class);

	private final boolean jobEnabled;
	private final UserRepository userRepository;

	public TokenRemovalScheduler(UserRepository userRepository,
								 				@Value("${token.removal.scheduler.enabled}") boolean jobEnabled) {
		this.userRepository = userRepository;
		this.jobEnabled = jobEnabled;
	}

	@Transactional
	@Scheduled(fixedRate = 30000)
	public void run() {
		if (jobEnabled) {
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - 10);
				int updated = userRepository.updateExpiredTokens(calendar.getTime());
				LOG.info("Numbers of user updated with NULL token (because of token expiration): " + updated);
			} catch (Exception exception) {
				LOG.info(exception.getMessage(), exception);
			}
		}
	}

}
