package com.example.authenticationservice.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.example.authenticationservice.dto.RegisterRequest;
import com.example.authenticationservice.entity.JWTDetails;
import com.example.authenticationservice.entity.UserDetails;
import com.example.authenticationservice.entity.UserGroup;
import com.example.authenticationservice.services.AuthServiceImpl;
import com.example.authenticationservice.services.UserServiceImpl;
import com.example.authenticationservice.util.CustomUtil;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    AuthServiceImpl authServiceImpl;

    @PostMapping("/create")
    public ResponseEntity<Object> createUser(
        @RequestHeader("authorization") String authorization,
        @RequestBody RegisterRequest request
    ){
        try {
           authServiceImpl.validate(CustomUtil.cleanToken(authorization));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(e.getMessage());
        }
        try {
            UserDetails userDetails = userServiceImpl.createUser(request);
            return ResponseEntity.ok().body(userDetails);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(
        @RequestHeader("authorization") String authorization,
        @RequestBody Map<String,String> updateRequest
    ){
         try {
           authServiceImpl.validate(CustomUtil.cleanToken(authorization));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(e.getMessage());
        }
        try {
            UserDetails userDetails = userServiceImpl.updateUser(updateRequest);
            return ResponseEntity.ok().body(userDetails);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getUsersByFilter(
        @RequestHeader("authorization") String authorization,
        @RequestParam("query") String query
    ){
        try {
           authServiceImpl.validate(CustomUtil.cleanToken(authorization));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(e.getMessage());
        }
        try {
            List<UserDetails> details = userServiceImpl.getUsersByFilter(query);
            return ResponseEntity.ok().body(details);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    } // needs to be changed


    @PostMapping("/addGroup")
    public ResponseEntity<Object> addUserGroup(
        @RequestBody String userGroupName,
        @RequestHeader("authorization") String authorization
    ) {
         try {
           authServiceImpl.validate(CustomUtil.cleanToken(authorization));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(e.getMessage());
        }
        try {
            UserGroup userGroup = userServiceImpl.addUserGroup(userGroupName);
            return ResponseEntity.ok().body(userGroup);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/addUsertoGroup")
    public ResponseEntity<Object> addUsertoGroup(
        @RequestBody Long userGroupId,
        @RequestHeader("authorization") String authorization
    ) {
        String token = CustomUtil.cleanToken(authorization);
         try {
           authServiceImpl.validate(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(e.getMessage());
        }
        JWTDetails details = new JWTDetails();
        try {
            details = authServiceImpl.getTokenDetails(token);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        try {
            UserGroup userGroup = userServiceImpl.addUsertoGroup(userGroupId,JWT.decode(details.getIdToken()).getClaim("user_name").asString());
            return ResponseEntity.ok().body(userGroup);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/removeUserGroup")
    public ResponseEntity<Object> removeUserGroup(
        @RequestBody Long userGroupId,
        @RequestHeader("authorization") String authorization
    ) {
         try {
           authServiceImpl.validate(CustomUtil.cleanToken(authorization));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(e.getMessage());
        }
        try {
            userServiceImpl.removeUserGroup(userGroupId);
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/removeUserfromGroup")
    public ResponseEntity<Object> removeUserfromGroup(
        @RequestBody Long userGroupId,
        @RequestHeader("authorization") String authorization
    ) {
        String token = CustomUtil.cleanToken(authorization);
         try {
           authServiceImpl.validate(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(e.getMessage());
        }
        try {
            userServiceImpl.removeUsertoGroup(userGroupId,JWT.decode(token).getSubject());
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }



    @GetMapping("/get")
    public ResponseEntity<Object> getUserByCondition(
        @RequestHeader("authorization") String authorization,
        @RequestParam("condition") String condition,
        @RequestParam("value") String value
    ){
        try {
           authServiceImpl.validate(CustomUtil.cleanToken(authorization));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(403)).body(e.getMessage());
        }
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
