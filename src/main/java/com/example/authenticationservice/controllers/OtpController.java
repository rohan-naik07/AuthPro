package com.example.authenticationservice.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.authenticationservice.entity.Otp;
import com.example.authenticationservice.services.OtpServiceImpl;

@RestController
@RequestMapping("/otp")
public class OtpController {

    private final OtpServiceImpl otpService;

    public OtpController(OtpServiceImpl otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Otp> generateOTP(@RequestParam Long userId) {
        Otp otp = otpService.generateOTP(userId);
        return ResponseEntity.ok(otp);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateOTP(@RequestParam Long userId,
                                               @RequestParam String otpCode) {
        boolean isValid = otpService.validateOTP(userId, otpCode);
        return ResponseEntity.ok(isValid);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteOTP(@RequestParam Long userId) {
        otpService.deleteOTP(userId);
        return ResponseEntity.noContent().build();
    }
}
