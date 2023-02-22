package com.javappa.startappa.startappalight.login.service;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.javappa.startappa.startappalight.login.component.tool.AuthoritiesConverter;
import com.javappa.startappa.startappalight.login.dto.response.SecurityResponseDTO;

@Service
public class SecurityServiceImpl implements SecurityService {

	private final AuthoritiesConverter authoritiesConverter;

	public SecurityServiceImpl(AuthoritiesConverter authoritiesConverter) {
		this.authoritiesConverter = authoritiesConverter;
	}

	@Override
	public SecurityResponseDTO createSecurityInfo(Principal principal) {

		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
		UserDetails userDetails = (UserDetails) usernamePasswordAuthenticationToken.getPrincipal();

		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		List<String> roles = authoritiesConverter.convert(authorities);
		SecurityResponseDTO securityDto = new SecurityResponseDTO(userDetails.getUsername(), roles);

		return securityDto;
	}

}
