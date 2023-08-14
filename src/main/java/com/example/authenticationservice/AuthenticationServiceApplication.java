package com.example.authenticationservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.authenticationservice.intf.UserService;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableJpaRepositories(basePackages = { "com.example.authenticationservice.repositories" })
@EnableAutoConfiguration(exclude = { FreeMarkerAutoConfiguration.class })
public class AuthenticationServiceApplication {

	Logger logger = LoggerFactory.getLogger(AuthenticationServiceApplication.class);

	@Autowired
	private UserService userService;

	@Value("${sp.authentication.issuer}")
	public String serviceName;

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServiceApplication.class, args);
	}

	@PostConstruct
	public void init(){
		try {
			//userService.createSuperAdminUserGroup();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("{} started",serviceName);
	}

	@Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
