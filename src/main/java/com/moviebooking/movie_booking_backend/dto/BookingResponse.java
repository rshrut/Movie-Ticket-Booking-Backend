package com.moviebooking.movie_booking_backend.dto;

import lombok.Data;

@Data
public class BookingResponse {
    private Long bookingId;
    private String confirmationCode;
    private String status = "CONFIRMED";
    private String message = "Booking successful.";
}