package com.moviebooking.movie_booking_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name= "movies")
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String posterUrl;
    private String genre;
    private Double rating;
    private Double price;
    private String city;

    @Lob
    private String description;
    private Double duration;
    private String language;

}
