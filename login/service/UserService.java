package com.javappa.startappa.startappalight.login.service;

import com.javappa.startappa.startappalight.login.dto.request.NewPasswordRequestDTO;
import com.javappa.startappa.startappalight.login.dto.request.ResetPasswordRequestDTO;
import com.javappa.startappa.startappalight.login.dto.request.TokenRequestDTO;
import com.javappa.startappa.startappalight.login.dto.response.EmailResponseDTO;
import com.javappa.startappa.startappalight.application.domain.User;

public interface UserService {

	void save(User user);
	
	EmailResponseDTO saveTokenAndSendEmail(TokenRequestDTO userDto);
	
	void validateToken(ResetPasswordRequestDTO resetPasswordRequestDTO);
	
	void saveNewPassword(NewPasswordRequestDTO newPasswordRequestDTO);
}