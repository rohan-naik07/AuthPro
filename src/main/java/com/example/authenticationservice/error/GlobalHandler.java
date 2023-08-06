package com.example.authenticationservice.error;

public class GlobalHandler extends Exception {
  
    public GlobalHandler(Exception e) throws Exception {
        e.printStackTrace();
        throw e;
    }
    
}
