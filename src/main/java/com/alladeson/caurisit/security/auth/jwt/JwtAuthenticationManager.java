package com.alladeson.caurisit.security.auth.jwt;

import io.jsonwebtoken.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.alladeson.caurisit.security.core.*;
import com.alladeson.caurisit.security.entities.Account;

import javax.servlet.http.HttpServletRequest;


@Component
public class JwtAuthenticationManager {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationManager.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountDetailsService accountDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;


    @Value("${security.jwt.jwtSecret}")
    private String jwtSecret;
    @Value("${security.jwt.jwtExpiration}")
    private long jwtExpiration;


    /**
     * @param auth
     * @return
     */
    public JwtAuthenticationResponse authenticate(AuthenticationPayload auth) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(auth.getLogin(), auth.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        JwtAuthenticationResponse token = this.generateToken(authentication);
        return token;
    }

    /**
     * @param request
     */
    public void authenticate(HttpServletRequest request) {

        String token = this.getTokenFromRequest(request);

        if (StringUtils.hasText(token) && this.validateToken(token)) {
            /* String */
            Long userId = this.getAccountIdFromToken(token);
            UserDetails userDetails = accountDetailsService.loadUserById(userId);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    /**
     *
     * @param request
     * @return
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private  /*String*/ Long getAccountIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret.getBytes())
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());/* claims.getSubject(); */
    }

    private JwtAuthenticationResponse generateToken(Authentication authentication) {
        //logger.info("---> authentication.principal: {}", authentication.getPrincipal());
        return generateToken((AccountDetails) authentication.getPrincipal());
    }

    private JwtAuthenticationResponse generateToken(AccountDetails userDetails) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        Map<String, Object> claims = new HashMap<>();
        List<String> roles = new ArrayList<>();
        userDetails.getAuthorities().forEach(role -> {
            roles.add(role.getAuthority());
        });
        claims.put("roles", roles.toArray(new String[0]));

        // JWT for authentication
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
                .compact();

        return new JwtAuthenticationResponse(token, expiryDate);
    }

    private boolean validateToken(String authToken) {
        return validateToken(authToken, jwtSecret);
    }

    private boolean validateToken(String authToken, String jwtRefreshSecret) {
        try {
            Jwts.parser().setSigningKey(jwtRefreshSecret.getBytes()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}
