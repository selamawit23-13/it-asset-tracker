package com.itassettracker.it_asset_tracker.service;

import com.itassettracker.it_asset_tracker.dto.*;
import com.itassettracker.it_asset_tracker.entity.*;
import com.itassettracker.it_asset_tracker.exception.ResourceNotFoundException;
import com.itassettracker.it_asset_tracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    public AuthService(UserRepository users, PasswordEncoder encoder) { this.users = users; this.encoder = encoder; }
    public UserResponse register(RegisterRequest request) {
        if (users.existsByUsername(request.getUsername().trim())) throw new IllegalArgumentException("Username is already registered.");
        User user = new User(request.getUsername().trim(), encoder.encode(request.getPassword()), Role.ROLE_STAFF);
        User saved = users.save(user);
        return new UserResponse(saved.getId(), saved.getUsername(), saved.getRole());
    }
    public User getUser(Long id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id)); }
}
