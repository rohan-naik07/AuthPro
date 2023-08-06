package com.example.authenticationservice.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authenticationservice.entity.VerifyToken;

@Repository
public interface VerifyTokenRepository extends JpaRepository<VerifyToken,String>{
    Optional<VerifyToken> findByEmail(String email) throws Exception;
    Optional<VerifyToken> findByToken(String token) throws Exception;
}
