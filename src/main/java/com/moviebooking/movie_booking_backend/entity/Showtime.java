package com.moviebooking.movie_booking_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"movie", "theatre", "bookings"}) // Safety first
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theatre;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private Double priceModifier = 1.0;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer seatsBooked = 0;

    @OneToMany(mappedBy = "showtime", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Booking> bookings;


    public Integer getAvailableSeats() {
        return this.totalSeats - this.seatsBooked;
    }
}