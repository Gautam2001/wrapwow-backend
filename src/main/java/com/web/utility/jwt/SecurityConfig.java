package com.web.utility.jwt;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.web.utility.CustomAccessDeniedHandler;
import com.web.utility.JwtAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService customUserDetailsService;
	private final JwtAuthenticationEntryPoint authenticationEntryPoint;
	private final CustomAccessDeniedHandler accessDeniedHandler;

	public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService,
			JwtAuthenticationEntryPoint authenticationEntryPoint, CustomAccessDeniedHandler accessDeniedHandler) {
		this.jwtUtil = jwtUtil;
		this.customUserDetailsService = customUserDetailsService;
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.accessDeniedHandler = accessDeniedHandler;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil, customUserDetailsService);

		return http
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
					.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					.requestMatchers("/member/ping", "/member/landingPageData", "/member/signup", "/member/login", "/member/sendOtp",
						"/member/validateOtp", "/member/forgotPassword", "/member/contactUs").permitAll()
					.requestMatchers("/admin/**").hasRole("ADMIN")
					.requestMatchers("/user/**").hasRole("USER")
					.requestMatchers("/member").hasAnyRole("ADMIN", "USER")
					.anyRequest().authenticated())
				.exceptionHandling(ex -> ex
					.authenticationEntryPoint(authenticationEntryPoint)
					.accessDeniedHandler(accessDeniedHandler))
				.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}


	@Bean
	CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();

	    config.setAllowedOrigins(List.of(
	        "http://localhost:5173",
	        "http://localhost:4173",
	        "https://wrap-and-wow.vercel.app",
	        "https://wrap-and-wow-gautam-singhals-projects.vercel.app"
	    ));

	    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

	    config.addAllowedHeader("*");
	    config.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	    return source;
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}