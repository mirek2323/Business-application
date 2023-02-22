package com.javappa.startappa.startappalight.login.component.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javappa.startappa.startappalight.login.component.tool.AuthoritiesConverter;
import com.javappa.startappa.startappalight.login.dto.response.SecurityResponseDTO;
import com.javappa.startappa.startappalight.login.repository.ItemRepository;
import com.javappa.startappa.startappalight.login.repository.UserRepository;
import com.javappa.startappa.startappalight.application.component.CurrentUser;
import com.javappa.startappa.startappalight.application.domain.User;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger LOG = LoggerFactory.getLogger(CustomAuthSuccessHandler.class);

	private final AuthoritiesConverter authoritiesConverter;
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;
	private final ItemRepository itemRepository;
	private final CurrentUser currentUser;

	public CustomAuthSuccessHandler(AuthoritiesConverter authoritiesConverter, ObjectMapper objectMapper,
			UserRepository userRepository, ItemRepository itemRepository, CurrentUser currentUser) {
		this.authoritiesConverter = authoritiesConverter;
		this.objectMapper = objectMapper;
		this.userRepository = userRepository;
		this.itemRepository = itemRepository;
		this.currentUser = currentUser;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		LOG.info("onAuthenticationSuccess - set status to HttpServletResponse.SC_OK");
		String responseBody = createResponse(authentication);

		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter responseWriter = response.getWriter();
		responseWriter.print(responseBody);
		responseWriter.flush();
	}

	private String createResponse(Authentication authentication) throws JsonProcessingException{

		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetails) {
			User user = userRepository.findByEmail(((UserDetails) authentication.getPrincipal()).getUsername());
			currentUser.setId(user.getId());
			setMaxFakeId(user);

			UserDetails userDetails = (UserDetails) principal;
			List<String> roles = authoritiesConverter.convert(userDetails.getAuthorities());
			SecurityResponseDTO securityDto = securityResponseDTO(userDetails, roles);

			String response = objectMapper.writeValueAsString(securityDto);
			LOG.info("createResponse: " + response);

			return response;
		}

		throw new IllegalArgumentException("Principal invalid");
	}

	// Pobierz z bazy i ustaw maksymalne id.
	// Jeśli użytkownik stworzył itemy, a potem wylogował się
	// i zalogował ponownie (a serwer aplikacji nie został w międzyczasie wyłączony)
	// musimy znać id ostatniego itema stworzonego przez tego usera.
	// W razie gdy użytkownik zechce stworzyć nowe itemy w ramach bieżącej sesji,
	// inkrementacja nastąpi od ostatnio stworzonego id itema.
	private void setMaxFakeId(User user) {
		Long maxFakeId = itemRepository.maxFakeId(user.getId());
		currentUser.setFakeId(maxFakeId);
	}

	private SecurityResponseDTO securityResponseDTO(UserDetails userDetails, List<String> roles) {
		SecurityResponseDTO securityDto = new SecurityResponseDTO(userDetails.getUsername(), roles);
		return securityDto;
	}

}