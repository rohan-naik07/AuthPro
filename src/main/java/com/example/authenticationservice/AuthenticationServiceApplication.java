package com.example.authenticationservice;

import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableJpaRepositories(basePackages = { "com.example.authenticationservice.repositories" })
@EnableDiscoveryClient
public class AuthenticationServiceApplication {

	Logger logger = LoggerFactory.getLogger(AuthenticationServiceApplication.class);

	@Value("${sp.authentication.issuer}")
	public String serviceName;

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServiceApplication.class, args);
	}

	@Bean
	public WebClient getWebClient(){
		return WebClient.builder()
		.clientConnector(new ReactorClientHttpConnector(getHttpClient()))
		.build();
	}

	@Bean
	public HttpClient getHttpClient(){
		return HttpClient.create()
		.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
		.responseTimeout(Duration.ofMillis(5000))
		.doOnConnected(
			conn -> 
		  		conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
				.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS))
		);
	}


	@PostConstruct
	public void init(){
		logger.info("{} started",serviceName);
	}

	@Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
