package com.example.authenticationservice.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserException extends GlobalHandler {
    
     Logger logger = LoggerFactory.getLogger(UserException.class);
    
    public UserException(Exception e) throws Exception{
        // log e.getMessage();
        super(e);
        logger.error(e.getMessage());
    }
}
