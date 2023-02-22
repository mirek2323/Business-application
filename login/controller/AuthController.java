package com.javappa.startappa.startappalight.login.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javappa.startappa.startappalight.login.dto.request.NewPasswordRequestDTO;
import com.javappa.startappa.startappalight.login.dto.request.ResetPasswordRequestDTO;
import com.javappa.startappa.startappalight.login.dto.request.TokenRequestDTO;
import com.javappa.startappa.startappalight.login.dto.response.EmailResponseDTO;
import com.javappa.startappa.startappalight.login.service.UserService;
import org.springframework.web.client.RestTemplate;


@RestController
@Validated
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/password/token")
    public ResponseEntity<EmailResponseDTO> sendEmailWithToken(@Valid @RequestBody TokenRequestDTO tokenRequestDTO){

        EmailResponseDTO emailResponseDTO = userService.saveTokenAndSendEmail(tokenRequestDTO);
        return new ResponseEntity<>(emailResponseDTO, HttpStatus.OK);
    }

    // Handles request coming from email link
    // ?token will map into ResetPasswordRequestDTO's token field
    // (please note that ResetPasswordRequestDTO is not followed by @RequestBody,
    // so it is not treated as mapping for body but as a container for params)
    @GetMapping("/password/reset")
    public void resetPassword(@Valid ResetPasswordRequestDTO resetPasswordRequestDTO, HttpServletResponse response) {
        try {
            userService.validateToken(resetPasswordRequestDTO);
            response.setHeader("Location", "http://localhost:4200/new-password/" + resetPasswordRequestDTO.getToken());
            response.setStatus(HttpStatus.FOUND.value());
        } catch (Exception tokenExpiredException) {
            response.setHeader("Location", "http://localhost:4200/tokenExpired");
            response.setStatus(HttpStatus.FOUND.value());
            // Found means that we expect such exception, and we have solution for that.
            // We invoke tokenExpired address in case of such situation.
        }
    }

    @PostMapping("/password/new")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordRequestDTO newPasswordRequestDTO) {
        try {
            userService.saveNewPassword(newPasswordRequestDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception tokenExpiredException) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
