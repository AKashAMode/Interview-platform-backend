package com.example.interview.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.interview.config.JwtUtil;
import com.example.interview.dto.LoginRequest;
import com.example.interview.dto.RegisterRequest;
import com.example.interview.entity.User;
import com.example.interview.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController { 

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody  RegisterRequest request) {
        try {
            
            if (userService.findByEmail(request.getEmail()).isPresent()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Email already registered");
                return ResponseEntity.badRequest().body(error);
            }

            User user = userService.registerUser(
               request.getFirstName(), 
                request.getLastName(),
                request.getEmail(), 
                request.getPassword()
              
            );
            
            String token = jwtUtil.generateToken(user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", Map.of(
                "id", user.getId(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail()
               
            ));
            response.put("message", "User registered successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody  LoginRequest request) {
        try {
           
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

           
            String token = jwtUtil.generateToken(request.getEmail());
            
            Optional<User> userOpt = userService.findByEmail(request.getEmail());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", Map.of(
                    "id", user.getId(),
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName(),
                    "email", user.getEmail()
                    
                ));
                response.put("message", "Login successful");
                
                return ResponseEntity.ok(response);
            }
            
            throw new RuntimeException("User not found after authentication");
            
        } catch (BadCredentialsException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            
            if (jwtUtil.validateToken(jwt)) {
                
                String email = jwtUtil.extractUsername(jwt);
                Optional<User> userOpt = userService.findByEmail(email);

                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("user", Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName()
                    ));
                    return ResponseEntity.ok(response);
                }
            }

            Map<String, Object> error = new HashMap<>();
            error.put("valid", false);
            error.put("error", "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("valid", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            
            if (jwtUtil.canTokenBeRefreshed(jwt)) {
                String newToken = jwtUtil.refreshToken(jwt);
                
                Map<String, Object> response = new HashMap<>();
                response.put("token", newToken);
                response.put("message", "Token refreshed successfully");
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Token cannot be refreshed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            
            if (jwtUtil.validateToken(jwt)) {
                String email = jwtUtil.extractUsername(jwt);
                Optional<User> userOpt = userService.findByEmail(email);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    Map<String, Object> response = new HashMap<>();
                    response.put("user", Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName()
                    ));
                    return ResponseEntity.ok(response);
                }
            }
            
            Map<String, Object> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

