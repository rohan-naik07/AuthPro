package com.example.authenticationservice.intf;

import java.util.List;
import java.util.Map;

import com.example.authenticationservice.dto.RegisterRequest;
import com.example.authenticationservice.entity.UserDetails;
import com.example.authenticationservice.entity.UserGroup;

import java.util.Optional;

public interface UserService {

    UserDetails createUser(RegisterRequest request) throws Exception;

    UserDetails updateUser(Map<String,String> request) throws Exception;

    List<UserDetails> getUsersByFilter(String query) throws Exception; // enum

    Optional<UserDetails> getUserByCondition(String condition,String userField) throws Exception; //enum

    UserGroup addUserGroup(String userGroupName) throws Exception;

    UserGroup createSuperAdminUserGroup() throws Exception;

    UserGroup addUsertoGroup(Long userGroupID,String userId) throws Exception;

    UserGroup removeUsertoGroup(Long userGroupID,String userId) throws Exception;

    void removeUserGroup(Long userGroupId) throws Exception;

    
}
