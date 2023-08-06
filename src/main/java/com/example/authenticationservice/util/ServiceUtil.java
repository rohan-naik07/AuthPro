package com.example.authenticationservice.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.example.authenticationservice.entity.VerifyToken;
import reactor.core.publisher.Mono;

@Service
public class ServiceUtil {
    
    @Autowired
    private WebClient webClient;

    @Value("")
    private String notificationServiceUrl;

    public Mono<String> sendVerificationEmail(String email,VerifyToken verifyToken) {
        return webClient.get()
            .uri(notificationServiceUrl)
            .retrieve()
            .bodyToMono(String.class)
            .onErrorMap(WebClientResponseException.class, this::handleError);
    }

    public Mono<String> sendPasswordChangeEmail(String email,VerifyToken verifyToken){
        return webClient.get()
            .uri(notificationServiceUrl)
            .retrieve()
            .bodyToMono(String.class)
            .onErrorMap(WebClientResponseException.class, this::handleError);
    }

    private Throwable handleError(WebClientResponseException ex) {
        // Handle the error and return a custom exception or appropriate response
        return ex;
    }
    
}
