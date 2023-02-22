package com.javappa.startappa.startappalight.login.service;

import java.security.Principal;

import com.javappa.startappa.startappalight.login.dto.response.SecurityResponseDTO;

public interface SecurityService {

	SecurityResponseDTO createSecurityInfo(Principal principal);

}