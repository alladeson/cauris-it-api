package com.alladeson.caurisit.security.auth.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alladeson.caurisit.security.core.AuthenticationPayload;


@RestController
@RequestMapping("/public")
public class JwtAuthController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JwtAuthenticationManager jwtAuthenticationManager;


    @PostMapping("/signin")
    public JwtAuthenticationResponse authenticate(@RequestBody AuthenticationPayload auth) {
        logger.trace("--> auth: {}", auth);

        return jwtAuthenticationManager.authenticate(auth);
    }

}
