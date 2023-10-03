package com.example.authenticationservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.example.authenticationservice.entity.JWTDetails;
import com.example.authenticationservice.entity.Role;
import com.example.authenticationservice.entity.User;
import com.example.authenticationservice.entity.UserDetails;
import com.example.authenticationservice.entity.UserGroup;
import com.example.authenticationservice.entity.VerifyToken;
import com.example.authenticationservice.error.AuthException;
import com.example.authenticationservice.intf.AuthService;
import com.example.authenticationservice.repositories.RoleRepository;
import com.example.authenticationservice.repositories.TokenRepository;
import com.example.authenticationservice.repositories.UserDetailsRepository;
import com.example.authenticationservice.repositories.UserGroupRepository;
import com.example.authenticationservice.repositories.UserRepository;
import com.example.authenticationservice.repositories.VerifyTokenRepository;
import com.example.authenticationservice.util.ServiceUtil;
import java.util.UUID;

// YWRtaW4sVGVzdDEyMw==

@Service
public class AuthServiceImpl implements AuthService{

    Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private VerifyTokenRepository verifyTokenRepository;

    @Autowired
    private  RoleRepository roleRepository;

    @Autowired
    private ServiceUtil serviceUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${sp.authentication.expireInterval}")
    private Integer expireInterval;

    @Value("${sp.authentication.issuer}")
    private String issuer;

    @Value("")
    private String audience;

    @Override
    @Cacheable(value = "token", key = "#accessToken")
    public JWTDetails getAccessToken(String creds) throws Exception {
        logger.debug("Fetching user from db...");
        byte[] decodedBytes = Base64.getDecoder().decode(creds);
        String[] userInfo = new String(decodedBytes).split(",");
        String username = userInfo[0];
        String password = userInfo[1];
        User user = new User();
        try {
            Optional<User> userOptional = userRepository.findByUserName(username);
            if(!userOptional.isPresent()){
                throw new AuthException(new Exception("User not found"));
            }
            user = userOptional.get();
        } catch (Exception e) {
            throw new AuthException(e);
        }
        logger.debug("Comparing passwords...");
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new AuthException(new Exception("Invalid password"));
        }
        try {
            String accessToken = createToken(user);
            JWTDetails tokenDetails = new JWTDetails();
            tokenDetails.setAccessToken(accessToken);
            tokenDetails.setRefreshToken(UUID.randomUUID().toString());
            tokenDetails.setRole("USER");
            tokenDetails.setIdToken(createIDtoken(user));
            tokenDetails.setIssuedAt(new java.sql.Date(System.currentTimeMillis()));
            tokenDetails.setExpireTime(System.currentTimeMillis() + 86400000);
            tokenRepository.save(tokenDetails);
            return tokenDetails;
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }

    @Override
    @CachePut(value = "token", key = "#oldTokenDetails.accessToken")
    public JWTDetails getAccessTokenFromRefreshToken(String refreshToken) throws Exception {
        logger.debug("Deleting old access token...");
        Optional<JWTDetails> oldTokenDetails = tokenRepository.getByRefreshToken(refreshToken);
        if(!oldTokenDetails.isPresent()){
            throw new AuthException(new Exception("Unable to find old refresh token"));
        }
        try {
            logger.debug("Fetching new access token...{}",oldTokenDetails.get().getAccessToken());
            tokenRepository.delete(oldTokenDetails.get());
            User user = userRepository.findById(JWT.decode(oldTokenDetails.get().getAccessToken()).getSubject()).get();
            String accessToken = createToken(user);
            JWTDetails tokenDetails = new JWTDetails();
            tokenDetails.setAccessToken(accessToken);
            tokenDetails.setRefreshToken(UUID.randomUUID().toString());
            tokenDetails.setIdToken(createIDtoken(user));
            tokenDetails.setRole("USER");
            tokenDetails.setIssuedAt(new java.sql.Date(System.currentTimeMillis()));
            tokenDetails.setExpireTime(System.currentTimeMillis() + 86400000);
            tokenRepository.save(tokenDetails);
            return tokenDetails;
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }

    @Override
    public JWTDetails saveTokenOAuth(JWTDetails details) throws Exception {
        logger.debug("Saving OAuth access token...");
        try {
            return tokenRepository.save(details);
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }

    @Override
    @CacheEvict(value = "token")
    public void revokeToken(String token) throws Exception {
        try {
            logger.debug("Revoking access token..."); 
            Optional<JWTDetails> tokenDetails = tokenRepository.getByAccessToken(token);
            if(!tokenDetails.isPresent()){
                throw new AuthException(new Exception("Unable to find token"));
            }
            tokenRepository.delete(tokenDetails.get());
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }

    @Override
    public void validateToken(String token) throws Exception{
        logger.debug("Validating access token...");
        Optional<JWTDetails> details = tokenRepository.getByAccessToken(token);
        if(!details.isPresent()){
            throw new AuthException(new Exception("Unable to find token"));
        }
        try {
            this.validate(token);
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }

    @Override
    public JWTDetails getTokenDetails(String token) throws Exception {
        logger.debug("Fetching access token details...");
        try {
            Optional<JWTDetails> details = tokenRepository.getByAccessToken(token);
            if(!details.isPresent()){
                throw new AuthException(new Exception("Unable to find token"));
            }
            return details.get();
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }

    public String createToken(User user) throws IllegalArgumentException, JWTCreationException, Exception {
        try {
            logger.debug("Creating access token...");
            Map<String,String> payload = new HashMap<>();
            payload.put("user_name",user.getUserName());
            payload.put("_id", user.getUserId().toString());
            payload.put("role","USER");
            Algorithm algorithm = Algorithm.HMAC256("secret");
            String token = JWT.create()
                .withIssuer(this.issuer)
                .withAudience(this.audience)
                .withPayload(payload)
                .withSubject(user.getUserId().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000))
                .sign(algorithm);
            return token;
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }




    public String createIDtoken(User user) throws Exception {
        try {
            logger.debug("Creating ID Token...");
            Algorithm algorithm = Algorithm.HMAC256("secret");
            Optional<UserDetails> details = userDetailsRepository.findById(user.getUserId());
            if(!details.isPresent()){
                throw new AuthException(new Exception("Unable to find user details"));
            }
            
            String token = JWT.create()
                .withIssuer(this.issuer)
                .withAudience(this.audience)
                .withSubject(user.getUserId().toString())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000))
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("name", details.get().getDisplayName())
                .withClaim("given_name",details.get().getDisplayName().split(" ")[0])
                .withClaim("family_name",details.get().getDisplayName().split(" ")[1])
                .withClaim("email",details.get().getEmail())
                .withClaim("picture",details.get().getProfilePicUrl())
                .sign(algorithm);
            return token;
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }

    public void validate(String token) throws Exception {
        try{
            logger.debug("Validating token details..");
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
        } catch (Exception e){
            // signatire invalid or token expired
            throw new AuthException(e);
        }
        Optional<JWTDetails> details = tokenRepository.getByAccessToken(token);
        if(!details.isPresent()) {
            throw new AuthException(new Exception("Unable to find token"));
        }
        Optional<User> user = userRepository.findById(JWT.decode(token).getSubject());
        if(!user.isPresent()) {
            throw new AuthException(new Exception("Unable to find user"));
        }
    }

    @Override
    public User createUser(String creds) throws Exception {
        // add user to default user group
        logger.debug("Creating new user for base 64 string {}...",creds);
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(creds);
            String[] userInfo = new String(decodedBytes).split(",");
            String username = userInfo[0];
            String password = userInfo[1];
            User user  = new User();
            String salt = UUID.randomUUID().toString();
            user.setUserName(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setSaltValue(salt); // for now
            user.setIsBlocked(0);
            user.setIsEmailVerified("false"); //enum
            user.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));
            user.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));
            return userRepository.save(user);
        } catch (Exception e) {
            throw new AuthException(e);
        }
        
    }

    @Override
    public User createSuperAdminUser(String creds) throws Exception {
        // add user to default user group
        Role role;
        UserGroup userGroup;
        Optional<UserGroup> userGroupOptional = userGroupRepository.findByName("super-admin");
        Optional<Role> roleOptional = roleRepository.findByName("super-admin");
        if (!roleOptional.isPresent()) {
            role = new Role();
            role.setName("super-admin");
            role.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));
            role = roleRepository.save(role);
        } else {
            role = roleOptional.get();
        }
        if (!userGroupOptional.isPresent()) {
            userGroup = new UserGroup();
            userGroup.setName("super-admin");
            userGroup.setRole(role);
            userGroup.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));
            userGroup = userGroupRepository.save(userGroup);
        } else {
            userGroup = userGroupOptional.get();
        }
        logger.info(userGroup.getName());
        logger.info(role.getName());
        logger.debug("Creating new user for base 64 string {}...",creds);
        try {
            String[] userInfo = creds.split(":");
            String username = userInfo[0];
            String password = userInfo[1];
            User user  = new User();
            String salt = UUID.randomUUID().toString();
            user.setUserName(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setSaltValue(salt); // for now
            user.setIsBlocked(0);
            user.setIsEmailVerified("false"); //enum
            user.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));
            user.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));
            return userRepository.save(user);
        } catch (Exception e) {
            throw new AuthException(e);
        }
        
    }

    @Override
    public User verifyEmail(String email,String token) throws Exception {
        logger.debug("Fetching verify token...");
        Optional<VerifyToken> verifyToken = verifyTokenRepository.findByEmail(email);
        if(!verifyToken.isPresent()) {
            throw new AuthException(new Exception("Unable to find verify token"));
        }
        logger.debug("Verifying email for token {}...",verifyToken.get().getToken());
       if(verifyToken.get().getToken().equals(token)){
            Optional<User> user = userDetailsRepository.getUserByEmail(email);
            if(!user.isPresent()) {
                throw new AuthException(new Exception("User not found"));
            }
            user.get().setIsEmailVerified("true");
            try {
                verifyTokenRepository.delete(verifyToken.get());
            } catch (Exception e) {
                throw new AuthException(e);
            }
            return userRepository.save(user.get());
       } else {
            throw new AuthException(new Exception("Incorrect verify token"));
       }
    }

    @Override
    public void sendVerificationEmail(String email) throws Exception {
        // TODO Auto-generated method stub
        Optional<User> user = userDetailsRepository.getUserByEmail(email);
        if(!user.isPresent()) {
            throw new AuthException(new Exception("User not found"));
        }
        logger.debug("Sending Verification Email to {}...", email);
        try {
            String token = JWT.create()
            .withIssuer(this.issuer)
            .withAudience(this.audience)
            .withSubject(user.get().getUserId().toString())
            .withIssuedAt(new Date(System.currentTimeMillis()))
            .withExpiresAt(new Date(System.currentTimeMillis() + expireInterval)).sign(Algorithm.HMAC256("secret"));
            
            VerifyToken verifyToken = new VerifyToken();
            verifyToken.setEmail(email);
            verifyToken.setToken(token);
            verifyToken.setOtp(null);
            verifyToken.setType("verify_email");
            verifyToken.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));
            verifyTokenRepository.save(verifyToken);

            try {
                this.serviceUtil.sendVerificationMail(email,verifyToken,user.get().getUserName());
            } catch (Exception e) {
                throw new AuthException(e);
            }

        } catch (Exception e) {
            throw new AuthException(e);
        }
        
    }


    @Override
    public void sendForgotPasswordEmail(String email) throws Exception{
        logger.debug("Sending forgot Password Mail to {} ...",email);
        Optional<User> user = userDetailsRepository.getUserByEmail(email);
        if(!user.isPresent()) {
            throw new AuthException(new Exception("Unable to find user"));
        }
        logger.debug("Creating verifcation token...");
        try {
            String token = JWT.create()
            .withIssuer(this.issuer)
            .withAudience(this.audience)
            .withSubject(user.get().getUserId().toString())
            .withIssuedAt(new Date(System.currentTimeMillis()))
            .withExpiresAt(new Date(System.currentTimeMillis() + 86400000)).sign(Algorithm.HMAC256("secret"));

            Random random = new Random();
            String otp = Integer.toString(random.nextInt(9000) + 1000);
            VerifyToken verifyToken = new VerifyToken();
            verifyToken.setEmail(email);
            verifyToken.setToken(token);
            verifyToken.setType("forgot_password"); // enum
            verifyToken.setOtp(otp);
            verifyToken.setCreatedAt(new java.sql.Date(System.currentTimeMillis()));
            verifyTokenRepository.save(verifyToken);
            logger.debug("Sending mail...");
            try {
                this.serviceUtil.sendChangePasswordMail(email, verifyToken);
            } catch (Exception e) {
                throw new AuthException(e);
            }
        } catch (Exception e) {
            throw new AuthException(e);
        }        
        
    }

    @Override
    public User changePassword(String token,String otp, String newPassword) throws Exception {
        logger.debug("Changing password for token : {}, otp : {}, new password : {}...",token,otp,newPassword);
        Optional<VerifyToken> details = verifyTokenRepository.findByToken(token);
        if(!details.isPresent()){
            throw new AuthException(new Exception("Unable to find verification token"));
        }

        try{
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
        } catch (Exception e){
            // signatire invalid or token expired
            throw new AuthException(e);
        }

        Optional<User> user = userRepository.findById(JWT.decode(details.get().getToken()).getSubject());
        if(!user.isPresent()){
            throw new AuthException(new Exception("Unable to find user"));
        }
        if(!details.get().getOtp().equals(otp)){
            throw new AuthException(new Exception("Invalid OTP"));
        }
        user.get().setPassword(passwordEncoder.encode(newPassword));
        try {
            verifyTokenRepository.delete(details.get());
        } catch (Exception e) {
            throw new AuthException(e);
        }
        return userRepository.save(user.get());
    }

    @Override
    public User blockUser(String userName) throws Exception {
        logger.debug("Blocking user {}...",userName);
        try {
            Optional<User> user = userRepository.findByUserName(userName);
            if(!user.isPresent()) {
                throw new AuthException(new Exception("Unable to find user"));
            }
            user.get().setIsBlocked(1);
            return userRepository.save(user.get());
        } catch (Exception e) {
            throw new AuthException(e);
        }
    }

}

