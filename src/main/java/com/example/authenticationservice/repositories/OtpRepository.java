package com.example.authenticationservice.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.authenticationservice.entity.Otp;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    List<Otp> findByUserId(Long userId);

    List<Otp> findByUserIdAndExpirationTimeAfter(Long userId, LocalDateTime currentTime);

    void deleteByUserId(Long userId);
}
