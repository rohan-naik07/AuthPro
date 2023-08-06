package com.example.authenticationservice.intf;

import java.util.List;
import java.util.Map;

import com.example.authenticationservice.dto.RegisterRequest;
import com.example.authenticationservice.entity.UserDetails;
import java.util.Optional;

public interface UserService {

    UserDetails createUser(RegisterRequest request) throws Exception;

    UserDetails updateUser(Map<String,String> request) throws Exception;

    List<UserDetails> getUsersByFilter(String query) throws Exception; // enum

    Optional<UserDetails> getUserByCondition(String condition,String userField) throws Exception; //enum

}
