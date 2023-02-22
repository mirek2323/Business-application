package com.javappa.startappa.startappalight.login.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javappa.startappa.startappalight.login.dto.response.SecurityResponseDTO;
import com.javappa.startappa.startappalight.login.service.SecurityService;

@RestController
@RequestMapping("/api/home")
public class HomeController {

	private final SecurityService securityService;

	public HomeController(SecurityService securityService) {
		this.securityService = securityService;
	}

	@GetMapping("/browse")
	public ResponseEntity<SecurityResponseDTO> browse(Principal principal) {
		SecurityResponseDTO securityDto = securityService.createSecurityInfo(principal);
		return new ResponseEntity<>(securityDto, HttpStatus.OK);
	}
}
