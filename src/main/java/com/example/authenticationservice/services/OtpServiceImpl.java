package com.example.authenticationservice.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.authenticationservice.entity.Otp;
import com.example.authenticationservice.repositories.OtpRepository;

@Service
public class OtpServiceImpl {

    @Autowired
    private OtpRepository otpRepository;

    public OtpServiceImpl(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Cacheable(value = "otp", key = "#id")
    public Otp generateOTP(Long userId) {
        int otpLength = 4;
        int expirationMinutes = 60;
        String otpCode = generateRandomOTP(otpLength);
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(expirationMinutes);

        Otp otp = new Otp();
        otp.setUserId(userId);
        otp.setOtpCode(otpCode);
        otp.setExpirationTime(expirationTime);

        return otpRepository.save(otp);
    }

    public boolean validateOTP(Long userId, String otpCode) {
        List<Otp> otps = otpRepository.findByUserIdAndExpirationTimeAfter(userId, LocalDateTime.now());
        for (Otp otp : otps) {
            if (otp.getOtpCode().equals(otpCode)) {
                return true;
            }
        }
        return false;
    }

    @CacheEvict(key = "otp")
    public void deleteOTP(Long userId) {
        otpRepository.deleteByUserId(userId);
    }

    private String generateRandomOTP(int length) {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }
}
