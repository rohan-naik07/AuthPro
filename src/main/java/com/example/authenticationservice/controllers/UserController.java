package com.example.authenticationservice.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    private Authentication authentication;

    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@RequestBody RegisterRequest request){
        try {
            UserDetails userDetails = userServiceImpl.createUser(request);
            return ResponseEntity.ok().body(userDetails);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(@RequestBody Map<String,String> updateRequest){
        try {
            UserDetails userDetails = userServiceImpl.updateUser(updateRequest);
            return ResponseEntity.ok().body(userDetails);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getUsersByFilter(@RequestParam("query") String query){
        try {
            List<UserDetails> details = userServiceImpl.getUsersByFilter(query);
            return ResponseEntity.ok().body(details);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    } 


    @PostMapping("/addGroup")
    public ResponseEntity<Object> addUserGroup(@RequestBody String userGroupName) {
        try {
            UserGroup userGroup = userServiceImpl.addUserGroup(userGroupName);
            return ResponseEntity.ok().body(userGroup);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/addUsertoGroup")
    public ResponseEntity<Object> addUsertoGroup(@RequestBody Long userGroupId) {
        try {
            UserGroup userGroup = userServiceImpl.addUsertoGroup(userGroupId,authentication.getPrincipal().toString());
            return ResponseEntity.ok().body(userGroup);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/removeUserGroup")
    public ResponseEntity<Object> removeUserGroup(@RequestBody Long userGroupId) {
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
        @RequestBody Long userGroupId
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
        @RequestParam("value") String value
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
