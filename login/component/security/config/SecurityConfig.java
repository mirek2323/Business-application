package com.javappa.startappa.startappalight.login.component.security.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.javappa.startappa.startappalight.login.component.security.CustomAuthEntryPoint;
import com.javappa.startappa.startappalight.login.component.security.CustomAuthFailureHandler;
import com.javappa.startappa.startappalight.login.component.security.CustomAuthSuccessHandler;
import com.javappa.startappa.startappalight.login.component.security.CustomLogoutSuccessHandler;
import com.javappa.startappa.startappalight.application.component.MD5Encoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private CustomAuthSuccessHandler authSuccessHandler;
	private CustomAuthFailureHandler authFailureHandler;
	private CustomLogoutSuccessHandler logoutSuccessHandler;
	private CustomAuthEntryPoint authEntryPoint;
	private DataSource dataSource;
	private MD5Encoder md5Encoder;

	public SecurityConfig(CustomAuthSuccessHandler authSuccessHandler, CustomAuthFailureHandler authFailureHandler,
			CustomLogoutSuccessHandler logoutSuccessHandler, CustomAuthEntryPoint authEntryPoint, DataSource dataSource,
			MD5Encoder md5Encoder) {
		this.authSuccessHandler = authSuccessHandler;
		this.authFailureHandler = authFailureHandler;
		this.logoutSuccessHandler = logoutSuccessHandler;
		this.authEntryPoint = authEntryPoint;
		this.dataSource = dataSource;
		this.md5Encoder = md5Encoder;
	}

	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

		http.cors().configurationSource(corsConfigurationSource()).and().csrf().csrfTokenRepository(csrfTokenRepository()).disable()
				.headers().frameOptions().sameOrigin()
				.and().addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
				.authorizeHttpRequests().requestMatchers("/", "/*", "/console/**").permitAll()
				.requestMatchers("/api/**").authenticated().and()
				.formLogin().successHandler(authSuccessHandler).failureHandler(authFailureHandler)
				.usernameParameter("email").passwordParameter("password").permitAll().and().exceptionHandling()
				.authenticationEntryPoint(authEntryPoint).and().logout().invalidateHttpSession(true)
				.deleteCookies("JSESSIONID").logoutSuccessHandler(logoutSuccessHandler);
		return http.build();
	}

	@Bean
	public AuthenticationManager authManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.jdbcAuthentication()
				.dataSource(dataSource)
				.usersByUsernameQuery("select email, password, active from users where email=?")
				.authoritiesByUsernameQuery("select u.email, r.role_name from users_roles ur left join users u on u.id=ur.user_id left join "
						+ "roles r on r.id=ur.role_id where email=?")
				.passwordEncoder(passwordEncoder()).and().build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new PasswordEncoder() {

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return md5Encoder.getMD5Hash(rawPassword.toString()).equals(encodedPassword);
			}

			@Override
			public String encode(CharSequence rawPassword) {
				return md5Encoder.getMD5Hash(rawPassword.toString());
			}
		};
	}

	private CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	private Filter csrfHeaderFilter() {
		return new OncePerRequestFilter() {

			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
											FilterChain filterChain) throws ServletException, IOException {

				CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
				if (csrf != null) {
					Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
					String token = csrf.getToken();
					if (cookie == null || token != null && !token.equals(cookie.getValue())) {
						cookie = new Cookie("XSRF-TOKEN", token);
						cookie.setPath("/");
						cookie.setDomain("localhost");
						response.addCookie(cookie);
					}
				}
				filterChain.doFilter(request, response);
			}
		};
	}

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}

}
