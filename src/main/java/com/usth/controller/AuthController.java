package com.usth.controller;

import com.usth.entity.User;
import com.usth.payload.JwtResponse;
import com.usth.payload.LoginRequest;
import com.usth.payload.RegisterRequest;
import com.usth.repository.UserRepository;
import com.usth.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final PasswordEncoder encoder;
        private final JwtUtils jwtUtils;

        @PostMapping("/login")
        public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
                System.out.println(">>> LOGIN ATTEMPT: " + loginRequest.getUsername()); // DEBUG LOG

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication
                                .getPrincipal();
                User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

                return ResponseEntity.ok(new JwtResponse(jwt,
                                user.getId(),
                                user.getUsername(),
                                user.getFullName(),
                                user.getRole()));
        }

        @PostMapping("/register")
        public ResponseEntity<?> registerUser(@RequestBody RegisterRequest signUpRequest) {
                if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                        return ResponseEntity
                                        .badRequest()
                                        .body("Error: Username is already taken!");
                }

                // Create new user's account
                User user = User.builder()
                                .username(signUpRequest.getUsername())
                                .fullName(signUpRequest.getFullName())
                                .password(encoder.encode(signUpRequest.getPassword()))
                                .userLocation(signUpRequest.getUserLocation()) // Lưu location
                                .email(signUpRequest.getEmail()) // Set email from request
                                .role("USER")
                                .build();

                userRepository.save(user);

                return ResponseEntity.ok("User registered successfully!");
        }

        @GetMapping("/me")
        public ResponseEntity<?> getCurrentUser() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !authentication.isAuthenticated()
                                || authentication.getPrincipal().equals("anonymousUser")) {
                        return ResponseEntity.badRequest().body("Not authenticated");
                }

                org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication
                                .getPrincipal();
                User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

                return ResponseEntity.ok(user);
        }
}
