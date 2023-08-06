package com.example.authenticationservice.services;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.authenticationservice.dto.RegisterRequest;
import com.example.authenticationservice.entity.User;
import com.example.authenticationservice.entity.UserDetails;
import com.example.authenticationservice.error.UserException;
import com.example.authenticationservice.intf.UserService;
import com.example.authenticationservice.repositories.UserDetailsRepository;
import com.example.authenticationservice.repositories.UserRepository;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${sp.authentication.admin_user_id}")
    private String adminUserId;

    @Override
    public UserDetails createUser(RegisterRequest request) throws Exception {
        logger.debug("Creating new user for request {}",request);
        try {
            Optional<User> user = userRepository.findById(request.getUserId());
            if(!user.isPresent()) {
                throw new UserException(new Exception("User not found"));
            }
            try {
                UserDetails details = new UserDetails();
                details.setBirthDate(request.getBirthDate());
                details.setDisplayName(request.getDisplayName());
                details.setEmail(request.getEmail());
                details.setNumber(request.getPhoneNumber());
                details.setProfilePicUrl(request.getProfilePicUrl());
                details.setUser(user.get());
                details.setRole("USER");
                details.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));
                details.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));
                return userDetailsRepository.save(details);
            } catch (Exception e) {
                throw new UserException(e);
            }
        } catch (Exception e) {
            throw new UserException(e);
        }
    }

    @Override
    public UserDetails updateUser(Map<String,String> request) throws Exception {
        logger.debug("Updating user for request {}",request);
        try {
            Optional<UserDetails> userDetails = userDetailsRepository.findById(request.get("_id"));
            for(String field : request.keySet()){
                switch(field){
                    case "email":
                        userDetails.get().setEmail(request.get(field));
                        break;
                    case "displayName":
                        userDetails.get().setDisplayName(request.get(field));
                        break;
                    case "birthDate":
                        userDetails.get().setBirthDate(Date.valueOf(request.get(field)));
                        break;
                    case "profile_pic_url":
                        userDetails.get().setProfilePicUrl(request.get(field));
                        break;
                    default : 
                        throw new UserException(new Exception("Invalid Field"));
                }
            }
            return userDetailsRepository.save(userDetails.get());
        } catch (Exception e) {
            throw new UserException(e);
        }
    }


    @Override
    public List<UserDetails> getUsersByFilter(String query) throws Exception {
        logger.debug("Getting users for {}",query);
        String queryString = new StringBuilder().append("%").append(query).append("%").toString();
        try {
            List<UserDetails> users = userDetailsRepository.findByFilters(queryString);
            return users;
        } catch (Exception e) {
            throw new UserException(e);
        }
    }

    @Override
    public Optional<UserDetails> getUserByCondition(String condition, String userField) throws Exception {
        logger.debug("Getting user by condition {} : {}",condition, userField);
        try {
            switch(condition){
                case "userId":
                    return userDetailsRepository.findById(userField);
                case "email":
                    return userDetailsRepository.findByEmail(userField);
                case "displayName":
                    return userDetailsRepository.findByDisplayName(userField);
                case "birthDate":
                    return userDetailsRepository.findByBirthDate(userField);
                case "profile_pic_url":
                    return userDetailsRepository.findByProfilePicUrl(userField);
                default : 
                throw new UserException(new Exception("Invalid Field"));
            }
        } catch (Exception e) {
            throw new UserException(e);
        }
    }
}