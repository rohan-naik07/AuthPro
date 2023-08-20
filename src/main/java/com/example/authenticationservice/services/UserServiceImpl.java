package com.example.authenticationservice.services;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.authenticationservice.dto.RegisterRequest;
import com.example.authenticationservice.entity.Role;
import com.example.authenticationservice.entity.User;
import com.example.authenticationservice.entity.UserDetails;
import com.example.authenticationservice.entity.UserGroup;
import com.example.authenticationservice.error.UserException;
import com.example.authenticationservice.intf.UserService;
import com.example.authenticationservice.repositories.RoleRepository;
import com.example.authenticationservice.repositories.UserDetailsRepository;
import com.example.authenticationservice.repositories.UserGroupRepository;
import com.example.authenticationservice.repositories.UserRepository;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private RoleRepository roleRepository;


    // add user group
    @Override
    public UserGroup addUserGroup(String userGroupName) throws Exception {
        UserGroup userGroup = new UserGroup();
        userGroup.setName(userGroupName);
        userGroup.setCreatedAt(new Date(System.currentTimeMillis()));
        return userGroupRepository.save(userGroup);
    }

    @Override
    public UserGroup createSuperAdminUserGroupandRole() throws Exception {
        Role role;
        Optional<UserGroup> userGroupOptional = userGroupRepository.findByName("super-admin");
        Optional<Role> roleOptional = roleRepository.findByName("super-admin");
        if (!roleOptional.isPresent()) {
            role = new Role();
            role.setName("super-admin");
            role.setCreatedAt(new Date(System.currentTimeMillis()));
        }
        if (!userGroupOptional.isPresent()) {
            role = roleOptional.get();
            UserGroup userGroup = new UserGroup();
            userGroup.setName("super-admin");
            userGroup.setRole(role);
            userGroup.setCreatedAt(new Date(System.currentTimeMillis()));
            return userGroupRepository.save(userGroup);
        }
        return userGroupOptional.get();
    }

    // add user to user group
    @Override
    public UserGroup addUsertoGroup(Long userGroupID,String userId) throws Exception {
        User user = userRepository.findById(userId)
        .orElseThrow(()->{
            try {
                return new UserException(new Exception("User not found"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        UserGroup userGroup = userGroupRepository.findById(userGroupID)
        .orElseThrow(()->{
            try {
                return new UserException(new Exception("User group not found"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        userGroup.getUsers().add(user);
        user.getUserGroups().add(userGroup);
        userRepository.save(user);
        return userGroupRepository.save(userGroup);
    }

    // remove user from user group
    @Override
    public UserGroup removeUsertoGroup(Long userGroupID,String userId) throws Exception {
        User user = userRepository.findById(userId)
        .orElseThrow(()->{
            try {
                return new UserException(new Exception("User not found"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        UserGroup userGroup = userGroupRepository.findById(userGroupID)
        .orElseThrow(()->{
            try {
                return new UserException(new Exception("User group not found"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        userGroup.getUsers().remove(user);
        user.getUserGroups().remove(userGroup);
        userRepository.save(user);
        return userGroupRepository.save(userGroup);
    }

    // remove user group 
    @Override
    public void removeUserGroup(Long userGroupId) throws Exception {
         UserGroup userGroup = userGroupRepository.findById(userGroupId)
        .orElseThrow(()->{
            try {
                return new UserException(new Exception("User group not found"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        userGroupRepository.delete(userGroup);
    }

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
            userDetails.get().setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));
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