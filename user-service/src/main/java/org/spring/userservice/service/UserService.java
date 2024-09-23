package org.spring.userservice.service;


import lombok.RequiredArgsConstructor;
import org.spring.userservice.model.User;
import org.spring.userservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<String> registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    public ResponseEntity<String> login(User user) {
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            User foundUser = optionalUser.get();
            if (passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
                String token = jwtTokenProvider.generateToken(user.getUsername(), foundUser.getId());
                return ResponseEntity.ok(token);
            }
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    public ResponseEntity<Boolean> validate(String header) {
        String token = header.startsWith("Bearer ") ? header.substring(7) : header;
        try {
            boolean isValid = jwtTokenProvider.validateToken(token) != null;
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(false);
        }
    }
}
