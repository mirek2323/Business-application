package com.javappa.startappa.startappalight.login.dto.request;

import jakarta.validation.constraints.NotNull;

public class ResetPasswordRequestDTO {

	@NotNull
	private String token;
	
	public ResetPasswordRequestDTO() {
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
