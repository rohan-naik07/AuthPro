package com.example.authenticationservice.util;

public class CustomUtil {

    public static boolean isJWTTokenExpired(String token) {
        return true;
    }
    
    public static String cleanToken(String token){
        return token.replaceAll("Bearer ","");
    }
}
