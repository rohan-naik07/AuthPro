package com.example.authenticationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig  {

     @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(new TokenFilter(), UsernamePasswordAuthenticationFilter.class);
        // @formatter:off
        http.authorizeHttpRequests()
        .requestMatchers("/auth/**").permitAll()
        .requestMatchers("/otp/**").permitAll()
        .anyRequest().fullyAuthenticated();
        // @formatter:on
        http.csrf().disable();
        return http.build();
    }

}
