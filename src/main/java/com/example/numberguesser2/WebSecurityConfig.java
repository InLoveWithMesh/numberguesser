package com.example.numberguesser2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity()
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/", "/home", "/h2-console", "/h2-console/", "", "/h2-console/**").permitAll()
					.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll()
			)
			.logout((logout) -> logout.permitAll())
				.csrf().disable();


		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails userMoritz =
			 User.withDefaultPasswordEncoder()
					 .username("moritz")
					 .password("123")
					 .roles("USER")
					 .build();

		UserDetails userLuca =
				User.withDefaultPasswordEncoder()
						.username("luca")
						.password("456")
						.roles("USER")
						.build();

		return new InMemoryUserDetailsManager(userMoritz, userLuca);
	}
}
