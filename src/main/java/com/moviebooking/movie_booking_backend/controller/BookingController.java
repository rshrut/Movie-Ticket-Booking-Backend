package com.moviebooking.movie_booking_backend.controller;

import com.moviebooking.movie_booking_backend.dto.BookingRequest;
import com.moviebooking.movie_booking_backend.dto.BookingResponse;
import com.moviebooking.movie_booking_backend.entity.Booking;
import com.moviebooking.movie_booking_backend.entity.Showtime;
import com.moviebooking.movie_booking_backend.entity.User;
import com.moviebooking.movie_booking_backend.repository.BookingRepository;
import com.moviebooking.movie_booking_backend.repository.ShowtimeRepository;
import com.moviebooking.movie_booking_backend.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional; // Correct Import
import org.springframework.security.core.Authentication; // Correct Import
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {
    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;

    public BookingController(
            BookingRepository bookingRepository,
            ShowtimeRepository showtimeRepository,
            UserRepository userRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<List<Map<String, Object>>> getUserBooking(Authentication authentication){

        User user = (User) authentication.getPrincipal();
        System.out.println("user" + user);
        List<Booking> userBookings =  bookingRepository.findByUserOrderByBookingTimeDesc(user);

        List<Map<String, Object>> response = userBookings.stream().map(booking -> {
            Map<String, Object> map = new HashMap<>();
            map.put("bookingId", booking.getId());
            map.put("movieTitle", booking.getShowtime().getMovie().getTitle());
            map.put("moviePoster", booking.getShowtime().getMovie().getPosterUrl());
            map.put("theatreName", booking.getShowtime().getTheatre().getName());
            map.put("showTime", booking.getShowtime().getStartTime());
            map.put("seats", booking.getSeatsBooked());
            map.put("totalAmount", booking.getTotalPrice());
            map.put("status", booking.getStatus());
            map.put("bookingDate", booking.getBookingTime());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookingById(@PathVariable Long id){
        return bookingRepository.findById(id)
                .map(booking -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("bookingId", booking.getId());
                    response.put("movie", booking.getShowtime().getMovie());
                    response.put("theatreName", booking.getShowtime().getTheatre());
                    response.put("showTime", booking.getShowtime().getStartTime());
                    response.put("selectedSeats", Arrays.asList(booking.getSeatsBooked().split(",")));
                    response.put("totalAmount",booking.getTotalPrice());
                    response.put("bookingDate", booking.getBookingTime());
                    response.put("confirmationCode", "TKT-" + booking.getId());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/occupied/{showtimeId}")
    public ResponseEntity<Set<String>> getOccupiedSeats(@PathVariable Long showtimeId) {
        return ResponseEntity.ok(getOccupiedSeatsForShowtime(showtimeId));
    }


    @Transactional
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request, Authentication authentication){
        User user = (User)authentication.getPrincipal();

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found for ID: " + request.getShowtimeId()));


        String[] requestedSeats = request.getSeatNumbers().toUpperCase().split(",");
        int requestedSeatCount = requestedSeats.length;

        // 1. Check if enough seats are available in total
        if (showtime.getAvailableSeats() < requestedSeatCount) {
            BookingResponse response = new BookingResponse();
            response.setStatus("FAILED");
            response.setMessage("Insufficient seats available.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // 2. Check for specific seat conflicts
        Set<String> currentlyOccupiedSeats = getOccupiedSeatsForShowtime(showtime.getId());
        Set<String> newRequestedSeats = new HashSet<>(Arrays.asList(requestedSeats));

        for (String seat : newRequestedSeats) {
            if (currentlyOccupiedSeats.contains(seat.trim())) {
                BookingResponse response = new BookingResponse();
                response.setStatus("FAILED");
                response.setMessage("Seat " + seat + " is already booked.");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
        }

        // 3. Save Booking
        Booking booking = new Booking();
        booking.setShowtime(showtime);
        booking.setUser(user);
        booking.setSeatsBooked(request.getSeatNumbers().toUpperCase());
        booking.setTotalPrice(request.getTotalAmount());
        booking.setBookingTime(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        // 4. Update Showtime Seat Count
        // Logic fixed: We increase the 'booked' count
        showtime.setSeatsBooked(showtime.getSeatsBooked() + requestedSeatCount);
        showtimeRepository.save(showtime);

        BookingResponse response = new BookingResponse();
        response.setBookingId(savedBooking.getId());
        response.setConfirmationCode("TKT-" + savedBooking.getId());
        response.setStatus(savedBooking.getStatus().toString());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Fixed: Now actually queries the database
    private Set<String> getOccupiedSeatsForShowtime(Long showtimeId) {
        List<String> bookedStrings = bookingRepository.findAllConfirmedSeatStringsByShowtimeId(showtimeId);
        Set<String> occupied = new HashSet<>();

        // "A1,A2" -> ["A1", "A2"] -> add to set
        for (String s : bookedStrings) {
            String[] split = s.split(",");
            for (String seat : split) {
                occupied.add(seat.trim());
            }
        }
        return occupied;
    }
}