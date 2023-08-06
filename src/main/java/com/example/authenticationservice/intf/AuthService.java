package com.example.authenticationservice.intf;
import com.example.authenticationservice.entity.JWTDetails;
import com.example.authenticationservice.entity.User;

public interface AuthService {
    
    JWTDetails getAccessToken(String username) throws Exception;

    JWTDetails getAccessTokenFromRefreshToken(String token) throws Exception;

    JWTDetails saveTokenOAuth(JWTDetails details) throws Exception;

    void revokeToken(String token) throws Exception;

    void validateToken(String token) throws Exception;
    
    JWTDetails getTokenDetails(String token) throws Exception;

    User createUser(String creds) throws Exception;

    User blockUser(String userName) throws Exception;

    User verifyEmail(String email,String token) throws Exception;

    User changePassword(String token,String otp,String newPassword) throws Exception;

    void sendForgotPasswordEmail(String email) throws Exception;

    void sendVerificationEmail(String email) throws Exception;
    
}
