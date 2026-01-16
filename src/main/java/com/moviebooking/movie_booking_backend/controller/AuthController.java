package com.moviebooking.movie_booking_backend.controller;

import com.moviebooking.movie_booking_backend.dto.AuthenticationResponse;
import com.moviebooking.movie_booking_backend.dto.LoginRequest;
import com.moviebooking.movie_booking_backend.dto.RegisterRequest;
import com.moviebooking.movie_booking_backend.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Updated Controller: Instead of returning plain strings, this controller
 * now returns an AuthenticationResponse object containing the JWT token.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Registers a new user and returns a token immediately for automatic login.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    /**
     * Authenticates credentials and returns a JWT token if successful.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody LoginRequest request
    ) {
        // The service handles the AuthenticationManager logic and JWT generation
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}


