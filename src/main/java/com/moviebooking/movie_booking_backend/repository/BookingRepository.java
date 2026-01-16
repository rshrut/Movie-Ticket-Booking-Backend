package com.moviebooking.movie_booking_backend.repository;

import com.moviebooking.movie_booking_backend.entity.Booking;
import com.moviebooking.movie_booking_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserOrderByBookingTimeDesc(User user);

    // Fetches the 'seatsBooked' string (e.g., "A1,A2") for all confirmed bookings of a showtime
    @Query("SELECT b.seatsBooked FROM Booking b WHERE b.showtime.id = :showtimeId AND b.status = 'CONFIRMED'")
    List<String> findAllConfirmedSeatStringsByShowtimeId(Long showtimeId);
}