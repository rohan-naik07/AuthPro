package com.example.authenticationservice.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthException extends GlobalHandler {
    Logger logger = LoggerFactory.getLogger(AuthException.class);
    
    public AuthException(Exception e) throws Exception{
        // log e.getMessage();
        super(e);
        logger.error(e.getMessage());
    }

}

