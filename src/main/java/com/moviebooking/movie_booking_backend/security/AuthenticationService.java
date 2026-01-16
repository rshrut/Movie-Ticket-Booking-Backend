package com.moviebooking.movie_booking_backend.security;

import com.moviebooking.movie_booking_backend.dto.AuthenticationResponse;
import com.moviebooking.movie_booking_backend.dto.LoginRequest;
import com.moviebooking.movie_booking_backend.dto.RegisterRequest;
import com.moviebooking.movie_booking_backend.entity.Role;
import com.moviebooking.movie_booking_backend.entity.User;
import com.moviebooking.movie_booking_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // Hash the password before saving!
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.USER);

        repository.save(user);

        return AuthenticationResponse.builder()
                .token(null)
                .message("User registered successfully. Registration successful! Please log in to continue.")
                .build();
    }

    public AuthenticationResponse authenticate(LoginRequest request) {
        // This method internally checks the password using our PasswordEncoder
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If we reach here, authentication was successful
        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User record not found"));

        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Login successful")
                .build();
    }
}
