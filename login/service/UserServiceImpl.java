package com.javappa.startappa.startappalight.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javappa.startappa.startappalight.login.component.email.EmailSender;
import com.javappa.startappa.startappalight.login.dto.request.NewPasswordRequestDTO;
import com.javappa.startappa.startappalight.login.dto.request.ResetPasswordRequestDTO;
import com.javappa.startappa.startappalight.login.dto.request.TokenRequestDTO;
import com.javappa.startappa.startappalight.login.dto.response.EmailResponseDTO;
import com.javappa.startappa.startappalight.login.repository.UserRepository;
import com.javappa.startappa.startappalight.application.component.MD5Encoder;
import com.javappa.startappa.startappalight.application.domain.User;

@Service("LoginUserServiceImpl")
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final EmailSender emailSender;
	private final MD5Encoder md5Encoder;

	public UserServiceImpl(UserRepository userRepository, EmailSender emailSender, MD5Encoder md5Encoder) {
		this.userRepository = userRepository;
		this.emailSender = emailSender;
		this.md5Encoder = md5Encoder;
	}

	@Override
	@Transactional
	public void save(User user) {
		userRepository.save(user);
	}

	@Override
	public EmailResponseDTO saveTokenAndSendEmail(TokenRequestDTO tokenRequestDTO) {
		User user = userRepository.findByEmail(tokenRequestDTO.getEmail());
		validate(user, "User not found by email");		
		user.generateToken();
		save(user);

		EmailResponseDTO emailDTO = emailSender.sendResetPasswordEmail(user);
		return emailDTO;
	}
	
	@Override
	public void validateToken(ResetPasswordRequestDTO resetPasswordRequestDTO) {
		User user = userRepository.findByToken(resetPasswordRequestDTO.getToken());
		validate(user, "Token is invalid or expired");	
	}

	@Override
	public void saveNewPassword(NewPasswordRequestDTO newPasswordRequestDTO) {
		User user = userRepository.findByToken(newPasswordRequestDTO.getToken());
		validate(newPasswordRequestDTO, user);
		user.setPassword(md5Encoder.getMD5Hash(newPasswordRequestDTO.getNewPassword()));
		user.setToken(null);
		user.setTokenTime(null);

		save(user);
	}
	
	private void validate(User user, String message) {
		if (user == null) {
			throw new RuntimeException(message);
		}
	}	
	
	private void validate(NewPasswordRequestDTO newPasswordRequestDTO, User user) {
		if (!newPasswordRequestDTO.getNewPassword().equals(newPasswordRequestDTO.getNewRepeatedPassword())) {
			throw new RuntimeException("New password doesn't match repeated one");
		}
	}

}
