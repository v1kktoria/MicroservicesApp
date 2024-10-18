package org.spring.userservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.http.auth.InvalidCredentialsException;
import org.spring.userservice.PasswordChangeProducer;
import org.spring.userservice.exception.ResourceAlreadyExistsException;
import org.spring.userservice.exception.ResourceNotFoundException;
import org.spring.userservice.model.User;
import org.spring.userservice.repository.UserRepository;
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
    private final PasswordChangeProducer passwordChangeProducer;

    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("User already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String login(User user) throws InvalidCredentialsException {
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            User foundUser = optionalUser.get();
            if (passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
                return jwtTokenProvider.generateToken(user.getUsername(), foundUser.getId());
            } else {
                throw new InvalidCredentialsException("Invalid credentials");
            }
        } else {
            throw new InvalidCredentialsException("User not found");
        }
    }

    public Boolean validate(String token) {
        try {
            return jwtTokenProvider.validateToken(token) != null;
        } catch (Exception e) {
            throw new RuntimeException("Invalid validate token", e);
        }
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) throws InvalidCredentialsException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                passwordChangeProducer.sendPasswordChangeNotification(user.getEmail());
            } else {
                throw new InvalidCredentialsException("Old password is incorrect");
            }
        } else {
            throw new ResourceNotFoundException("User not found");
        }
    }
}
