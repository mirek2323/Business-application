package com.javappa.startappa.startappalight.login.dto.response;

import java.util.ArrayList;
import java.util.List;

public class SecurityResponseDTO {

	private final String email;
	private final List<String> roles = new ArrayList<>();

	public SecurityResponseDTO(String email, List<String> roles) {
		this.email = email;
		this.roles.addAll(roles);
	}

	public List<String> getRoles() {
		return roles;
	}

	public String getEmail() {
		return email;
	}
}
