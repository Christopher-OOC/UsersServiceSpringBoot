package com.appsdeveloperblog.tutorials.junit.security;

import com.appsdeveloperblog.tutorials.junit.io.UserEntity;
import com.appsdeveloperblog.tutorials.junit.io.UsersRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    UsersRepository userRepository;

    public AuthorizationFilter(AuthenticationManager authManager,
                               UsersRepository userRepository) {
        super(authManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        System.out.println("Hello I can see it on the console!!");

        String header = req.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.HEADER_STRING);

        System.out.println("Hello I can see it on the console!!");

        if (token != null) {

            token = token.replace(SecurityConstants.TOKEN_PREFIX, "");

            String user = null;

//            String user = Jwts.parser()
//                    .setSigningKey( SecurityConstants.TOKEN_SECRET)
//                    .parseClaimsJws( token )
//                    .getBody()
//                    .getSubject();

            System.out.println("Heelo");

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, null);
            }

            return null;
        }

        return null;
    }

}