package com.moviebooking.movie_booking_backend.controller;

import com.moviebooking.movie_booking_backend.entity.Showtime;
import com.moviebooking.movie_booking_backend.entity.Theatre;
import com.moviebooking.movie_booking_backend.repository.ShowtimeRepository;
import com.moviebooking.movie_booking_backend.repository.TheatreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/showtimes")
public class ShowtimeController {

    private final ShowtimeRepository showtimeRepository;
    private final TheatreRepository theatreRepository;

    @Autowired
    public ShowtimeController(ShowtimeRepository showtimeRepository, TheatreRepository theatreRepository){
        this.showtimeRepository = showtimeRepository;
        this.theatreRepository = theatreRepository;
    }

    @GetMapping
    public ResponseEntity<String> rootCheck() {
        return ResponseEntity.ok("Showtimes API is active. Use /movie/{id} or /theatres endpoints.");
    }

    @GetMapping("/movie/{movieId}")
    public List<Showtime> getShowtimesByMovie(@PathVariable Long movieId) {
        // Uses the custom method defined in ShowtimeRepository
        return showtimeRepository.findByMovieId(movieId);
    }

    @GetMapping("/theatres")
    public List<Theatre> getAllTheatres(){
        return theatreRepository.findAll();
    }
}
