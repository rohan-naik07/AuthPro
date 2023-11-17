package com.example.authenticationservice.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.authenticationservice.dto.RegisterRequest;
import com.example.authenticationservice.entity.UserDetails;
import com.example.authenticationservice.entity.UserGroup;
import com.example.authenticationservice.services.UserServiceImpl;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @PostMapping("/create")
    public ResponseEntity<Object> createUser(
        @RequestBody RegisterRequest request,
        @RequestHeader("tenantId") String tenantId
    ){
        try {
            UserDetails userDetails = userServiceImpl.createUser(request);
            return ResponseEntity.ok().body(userDetails);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(
        @RequestBody Map<String,String> updateRequest,
        @RequestHeader("tenantId") String tenantId
    ){
        try {
            UserDetails userDetails = userServiceImpl.updateUser(updateRequest);
            return ResponseEntity.ok().body(userDetails);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getUsersByFilter(
        @RequestParam("query") String query,
        @RequestHeader("tenantId") String tenantId
    ){
        try {
            List<UserDetails> details = userServiceImpl.getUsersByFilter(query);
            return ResponseEntity.ok().body(details);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    } 


    @PostMapping("/addGroup")
    public ResponseEntity<Object> addUserGroup(
        @RequestBody String userGroupName,
        @RequestHeader("tenantId") String tenantId
    ) {
        try {
            UserGroup userGroup = userServiceImpl.addUserGroup(userGroupName);
            return ResponseEntity.ok().body(userGroup);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/addUsertoGroup/{userId}")
    public ResponseEntity<Object> addUsertoGroup(
        @RequestBody Long userGroupId,
        @PathVariable String userId,
        @RequestHeader("tenantId") String tenantId
    ) {
        try {
            UserGroup userGroup = userServiceImpl.addUsertoGroup(userGroupId,userId);
            return ResponseEntity.ok().body(userGroup);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/removeUserGroup")
    public ResponseEntity<Object> removeUserGroup(
        @RequestBody Long userGroupId,
        @RequestHeader("tenantId") String tenantId
    ) {
        try {
            userServiceImpl.removeUserGroup(userGroupId);
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/removeUserfromGroup/{userId}")
    public ResponseEntity<Object> removeUserfromGroup(
        @PathVariable String userId,
        @RequestBody Long userGroupId,
        @RequestHeader("tenantId") String tenantId
    ) {
        try {
            userServiceImpl.removeUsertoGroup(userGroupId,userId);
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getUserByCondition(
        @RequestParam("condition") String condition,
        @RequestParam("value") String value,
        @RequestHeader("tenantId") String tenantId
    ){
        try {
            Optional<UserDetails> details = userServiceImpl.getUserByCondition(condition, value);
            if (!details.isPresent()) {
                return ResponseEntity.internalServerError().body("User not found");
            }
            return ResponseEntity.ok().body(details);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
