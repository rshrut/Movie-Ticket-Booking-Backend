package com.moviebooking.movie_booking_backend.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "theatres")
@Data
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(nullable = false)
    private String city;

    private Integer seatingCapacity;
}
