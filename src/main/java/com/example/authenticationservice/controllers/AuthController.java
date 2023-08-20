package com.example.authenticationservice.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.authenticationservice.dto.RegisterRequest;
import com.example.authenticationservice.entity.JWTDetails;
import com.example.authenticationservice.entity.User;
import com.example.authenticationservice.entity.UserDetails;
import com.example.authenticationservice.services.AuthServiceImpl;
import com.example.authenticationservice.services.UserServiceImpl;

/*
@Operation(summary = "Get a book by its id")
@ApiResponses(value = { 
  @ApiResponse(responseCode = "200", description = "Found the book", 
    content = { @Content(mediaType = "application/json", 
      schema = @Schema(implementation = Book.class)) }),
  @ApiResponse(responseCode = "404", description = "Book not found", 
    content = @Content) })
 */

@CrossOrigin("*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthServiceImpl authServiceImpl;

    @Autowired
    UserServiceImpl userServiceImpl;

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody String credentials){
        try {
            JWTDetails details = authServiceImpl.getAccessToken(credentials);
            return ResponseEntity.ok().body(details);
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

     @PostMapping("/create")
    public ResponseEntity<Object> createUser(@RequestBody String credentials){
        try {
            User user = authServiceImpl.createUser(credentials);
            return ResponseEntity.ok().body(user);
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/createSuperAdmin")
    public ResponseEntity<Object> createSuperAdminUser(@RequestBody String credentials){
        try {
            User user = authServiceImpl.createSuperAdminUser(credentials);
            return ResponseEntity.ok().body(user);
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody RegisterRequest registerRequest){
        try {
            UserDetails userDetails = userServiceImpl.createUser(registerRequest);
            return ResponseEntity.ok().body(userDetails);
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Object> validateToken(@RequestBody String token){
        try {
            authServiceImpl.validate(token);
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/revoke")
    public ResponseEntity<Object> revokeToken(@RequestBody String token){
         try {
            authServiceImpl.revokeToken(token);
            return ResponseEntity.ok().body(token);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/details")
    public ResponseEntity<Object> tokenDetails(@RequestParam("token") String token){
         try {
            JWTDetails jwtDetails = authServiceImpl.getTokenDetails(token);
            return ResponseEntity.ok().body(jwtDetails);
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<Object> refreshToken(@RequestParam("refreshToken") String refreshToken){
         try {
            JWTDetails jwtDetails = authServiceImpl.getAccessTokenFromRefreshToken(refreshToken);
            return ResponseEntity.ok().body(jwtDetails);
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/validateEmail")
    public ResponseEntity<Object> validateEmail(@RequestParam("token") String token,@RequestParam("email") String email){
        try {
            User user = authServiceImpl.verifyEmail(email, token);
            return ResponseEntity.ok().body(user.getUserName());
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    
     @PostMapping("/sendVerificationEmail")
    public ResponseEntity<Object> sendVerificationEmail(@RequestBody String email){
       try {
            authServiceImpl.sendVerificationEmail(email);
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }   

    @PostMapping("/sendChangePasswordMail")
    public ResponseEntity<Object> sendChangePasswordMail(@RequestBody String email){
        try {
            authServiceImpl.sendForgotPasswordEmail(email);
            return ResponseEntity.ok().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    } 
    
    @PostMapping("/changePassword")
    public ResponseEntity<Object> changePassword(
        @RequestBody String newPassword,
        @RequestParam("verifyToken") String verifyToken,
        @RequestParam("otp") String otpString
    ){
       try {
            User user = authServiceImpl.changePassword(verifyToken, otpString, newPassword);
            return ResponseEntity.ok().body(user.getUserName());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    } 

    @PostMapping("/blockUser")
    public ResponseEntity<Object> blockUser(@RequestParam("userName") String userName){
        try {
            User user = authServiceImpl.blockUser(userName);
            return ResponseEntity.ok().body(user.getUserName());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
