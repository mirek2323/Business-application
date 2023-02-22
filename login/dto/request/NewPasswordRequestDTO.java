package com.javappa.startappa.startappalight.login.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NewPasswordRequestDTO {

	@NotNull
	@Size(min = 6, max = 10)
	private String newPassword;

	@NotNull
	private String newRepeatedPassword;

	@NotNull
	private String token;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewRepeatedPassword() {
		return newRepeatedPassword;
	}

	public void setNewRepeatedPassword(String newRepeatedPassword) {
		this.newRepeatedPassword = newRepeatedPassword;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public boolean isPasswordChangeDetected() {
		return this.newPassword != null;
	}	
}
