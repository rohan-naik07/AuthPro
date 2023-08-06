package com.example.authenticationservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authenticationservice.entity.JWTDetails;

@Repository
public interface TokenRepository extends JpaRepository<JWTDetails,String>{
    Optional<JWTDetails> getByAccessToken(String token);
    Optional<JWTDetails> getByRefreshToken(String token);
}
