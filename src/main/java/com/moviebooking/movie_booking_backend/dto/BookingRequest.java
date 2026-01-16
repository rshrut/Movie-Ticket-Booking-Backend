package com.moviebooking.movie_booking_backend.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long showtimeId;
    private String seatNumbers;
    private Double totalAmount;
}
