package com.example.authenticationservice.config;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.authenticationservice.entity.Role;
import com.example.authenticationservice.entity.UserGroup;
import com.example.authenticationservice.intf.AuthService;
import com.example.authenticationservice.intf.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class TokenFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenFilter.class);

    private AuthService authService;
    private UserService userService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) 
        throws IOException,ServletException {

        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String jwt = this.resolveToken(httpServletRequest);
            if (StringUtils.hasText(jwt)) {
                try {
                    this.authService.validateToken(jwt);
                     Authentication authentication = this.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                     LOGGER.info("Security exception {}",e.getMessage());
                    ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }

            filterChain.doFilter(servletRequest, servletResponse);

            this.resetAuthenticationAfterRequest();
        } catch (Exception eje) {
            LOGGER.info("Security exception {}",eje.getMessage());
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            LOGGER.debug("Exception " + eje.getMessage(), eje);
        }
    }

    public Authentication getAuthentication(String token) throws Exception {
        Set<Role> roles = new HashSet<>();
        DecodedJWT jwt = JWT.decode(token);
        com.example.authenticationservice.entity.User user = userService.getUserByCondition("userId",jwt.getSubject()).get().getUser();
        for(UserGroup userGroup : user.getUserGroups()){
            roles.add(userGroup.getRole());
        }
        Collection<? extends GrantedAuthority> authorities = roles.stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());

        User principal = new User(user.getUserName(),user.getPassword(),authorities);

        return new UsernamePasswordAuthenticationToken(principal, "");
    }

    private void resetAuthenticationAfterRequest() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String jwt = bearerToken.substring(7, bearerToken.length());
            return jwt;
        }
        return null;
    }

}