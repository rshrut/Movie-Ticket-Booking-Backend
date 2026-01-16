package com.moviebooking.movie_booking_backend.repository;

import com.moviebooking.movie_booking_backend.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {

}
