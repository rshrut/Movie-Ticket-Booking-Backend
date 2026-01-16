package com.moviebooking.movie_booking_backend;

import com.moviebooking.movie_booking_backend.entity.Movie;
import com.moviebooking.movie_booking_backend.entity.Showtime;
import com.moviebooking.movie_booking_backend.entity.Theatre;
import com.moviebooking.movie_booking_backend.entity.User;
import com.moviebooking.movie_booking_backend.repository.MovieRepository;
import com.moviebooking.movie_booking_backend.repository.ShowtimeRepository;
import com.moviebooking.movie_booking_backend.repository.TheatreRepository;
import com.moviebooking.movie_booking_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {


    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final ShowtimeRepository showtimeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(
            MovieRepository movieRepository,
            TheatreRepository theatreRepository,
            ShowtimeRepository showtimeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder){
        this.movieRepository = movieRepository;
        this.theatreRepository = theatreRepository;
        this.showtimeRepository = showtimeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (movieRepository.count() == 0) {
            System.out.println("Initializing Database with production seed data...");

            // 1. Create a Test User for the "My Bookings" page
            if (userRepository.findByEmail("test@booking.com").isEmpty()) {
                User testUser = new User();
                testUser.setName("Test User");
                testUser.setEmail("test@booking.com");
                testUser.setPassword(passwordEncoder.encode("password"));
                userRepository.save(testUser);
                System.out.println("Test user created: test@booking.com / password");
            }

            // 2. Create Movies
            Movie avengers = new Movie();
            avengers.setTitle("Avengers: Endgame");
            // Using external URLs to ensure posters show up correctly in the hosted app
            avengers.setPosterUrl("https://image.tmdb.org/t/p/w500/or06vS3STuS5jB0bpZxy7tpUunD.jpg");
            avengers.setGenre("Action, Sci-Fi");
            avengers.setRating(8.5);
            avengers.setPrice(250.00);
            avengers.setCity("Bangalore");
            avengers.setDescription("The Avengers assemble once more to reverse Thanos' actions and restore balance to the universe.");
            avengers.setDuration(181.0);
            avengers.setLanguage("English");

            Movie inception = new Movie();
            inception.setTitle("Inception");
            inception.setPosterUrl("https://image.tmdb.org/t/p/w500/o0jO19SvevAnS77YF7pA9Yv9vYI.jpg");
            inception.setGenre("Sci-Fi, Thriller");
            inception.setRating(8.8);
            inception.setPrice(200.00);
            inception.setCity("Mumbai");
            inception.setDescription("A thief who steals corporate secrets through the use of dream-sharing technology...");
            inception.setDuration(148.0);
            inception.setLanguage("English");

            List<Movie> savedMovies = movieRepository.saveAll(List.of(avengers, inception));
            Movie savedAvengers = savedMovies.get(0);
            Movie savedInception = savedMovies.get(1);

            // 3. Create Theatres
            Theatre pvr = new Theatre();
            pvr.setName("PVR Forum Mall");
            pvr.setCity("Bangalore");
            pvr.setSeatingCapacity(150);

            Theatre inox = new Theatre();
            inox.setName("INOX Garuda");
            inox.setCity("Bangalore");
            inox.setSeatingCapacity(100);

            List<Theatre> savedTheatres = theatreRepository.saveAll(List.of(pvr, inox));
            Theatre savedPvr = savedTheatres.get(0);
            Theatre savedInox = savedTheatres.get(1);

            // 4. Create Showtimes
            LocalDateTime tomorrowMorning = LocalDateTime.now()
                    .plusDays(1)
                    .withHour(10)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            Showtime s1 = new Showtime();
            s1.setMovie(savedAvengers);
            s1.setTheatre(savedPvr);
            s1.setStartTime(tomorrowMorning);
            s1.setTotalSeats(savedPvr.getSeatingCapacity());
            s1.setSeatsBooked(0);

            Showtime s2 = new Showtime();
            s2.setMovie(savedAvengers);
            s2.setTheatre(savedInox);
            s2.setStartTime(tomorrowMorning.plusHours(4));
            s2.setTotalSeats(savedInox.getSeatingCapacity());
            s2.setSeatsBooked(0);

            Showtime s3 = new Showtime();
            s3.setMovie(savedInception);
            s3.setTheatre(savedPvr);
            s3.setStartTime(tomorrowMorning.plusHours(2));
            s3.setTotalSeats(savedPvr.getSeatingCapacity());
            s3.setSeatsBooked(0);

            showtimeRepository.saveAll(List.of(s1, s2, s3));

            System.out.println("✅ Seed data successfully loaded: " + movieRepository.count() + " movies and " + showtimeRepository.count() + " showtimes.");
        } else {
            System.out.println("Database already contains data. Skipping initialization.");
        }
    }
}
