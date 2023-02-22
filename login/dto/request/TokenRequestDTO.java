package com.javappa.startappa.startappalight.login.dto.request;

import jakarta.validation.constraints.NotNull;

public class TokenRequestDTO {

	@NotNull
	private String email;
	
	public TokenRequestDTO() {
	}

	public String getEmail() {
		return email;
	}
}
