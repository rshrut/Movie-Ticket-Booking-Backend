package com.moviebooking.movie_booking_backend.config;

import com.moviebooking.movie_booking_backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // CRITICAL IMPORT
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider  authenticationProvider;

    @Value("${application.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of( "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // --- Security Filter Chain Configuration (Integration Point) ---

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)

                // 1. Session Management: Set to STATELESS for REST APIs using JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 2. Define Authentication Provider
                .authenticationProvider(authenticationProvider)

                // 3. Authorization Rules
                .authorizeHttpRequests(auth ->  auth
                        // Permit OPTIONS method globally (CORS preflight)

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // PUBLIC ENDPOINTS (Movies, Showtimes, Auth)
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/movies/**",
                                "/api/v1/movies",
                                "/api/v1/showtimes/**",
                                "/api/v1/showtimes",
                                "/api/v1/bookings/occupied/**"
                        ).permitAll()

                        // PROTECTED ENDPOINTS (Booking requires login)
//                        .requestMatchers(HttpMethod.POST, "/api/v1/bookings/**").authenticated()
//
//                        // Any other generic bookings requests (history, etc.)
//                        .requestMatchers("/api/v1/bookings/**").authenticated()

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )

                // 4. ADD JWT FILTER HERE
                // Add JwtAuthenticationFilter before the standard Username/Password filter
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}