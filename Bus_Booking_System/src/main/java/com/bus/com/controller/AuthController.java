package com.bus.com.controller;

import com.bus.com.dto.ApiResponse;
import com.bus.com.dto.UserDTO;
import com.bus.com.dto.AuthRequest;
import com.bus.com.dto.AuthResponse;
import com.bus.com.security.JwtUtils;
import com.bus.com.service.UserService;
import com.bus.com.security.CustomUserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    //Signup 
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserDTO dto) {
        ApiResponse response = userService.registerNewUser(dto);
        return ResponseEntity.ok(response);
    }

    //Signin
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest loginRequest) {
//    	System.out.println("Inside signin controller with email=" + loginRequest.getEmail());
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new AuthResponse("User authenticated successfully!", jwt));
    }
}
