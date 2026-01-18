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
import java.util.ArrayList;
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

        if (userRepository.findByEmail("test@booking.com").isEmpty()) {
            User testUser = new User();
            testUser.setName("Test User");
            testUser.setEmail("test@booking.com");
            testUser.setPassword(passwordEncoder.encode("password"));
            userRepository.save(testUser);
            System.out.println("Test user created: test@booking.com / password");
        }

        // 2. Seed Movies (Only if DB is empty)
        if (movieRepository.count() == 0) {
            System.out.println("Initializing Database with expanded seed data...");

            List<Movie> movies = new ArrayList<>();

            movies.add(createMovie("Avengers: Endgame",
                    "https://image.tmdb.org/t/p/w500/or06FN3Dka5tukK1e9sl16pB3iy.jpg",
                    "Action, Sci-Fi", 8.4, 250.0, "Bangalore",
                    "After the devastating events of Infinity War, the universe is in ruins. With the help of remaining allies, the Avengers assemble once more.", 181.0, "English"));

            movies.add(createMovie("Inception",
                    "https://image.tmdb.org/t/p/w500/edv5CZvWj09upOsy2Y6IwDhK8bt.jpg",
                    "Sci-Fi, Adventure", 8.8, 200.0, "Mumbai",
                    "Cobb, a skilled thief who steals secrets from deep within the subconscious during the dream state, is offered a chance at redemption.", 148.0, "English"));

            movies.add(createMovie("The Dark Knight",
                    "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                    "Action, Crime", 9.0, 220.0, "Bangalore",
                    "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest tests.", 152.0, "English"));

            movies.add(createMovie("Interstellar",
                    "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg\n",
                    "Sci-Fi, Drama", 8.7, 180.0, "Delhi",
                    "When Earth becomes uninhabitable, a team of scientists travel through a wormhole in search of a new home for mankind.", 169.0, "English"));

            movies.add(createMovie("Spider-Man: No Way Home",
                    "https://image.tmdb.org/t/p/w500/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg",
                    "Action, Adventure", 8.2, 240.0, "Bangalore",
                    "With Spider-Man's identity now revealed, Peter asks Doctor Strange for help. When a spell goes wrong, foes from other worlds appear.", 148.0, "English"));

            movieRepository.saveAll(movies);

            // 3. Seed Theatres
            List<Theatre> theatres = new ArrayList<>();
            theatres.add(createTheatre("PVR: Forum Mall", "Koramangala, Bangalore", 150));
            theatres.add(createTheatre("INOX: Mantri Square", "Malleshwaram, Bangalore", 120));
            theatres.add(createTheatre("Cinepolis: Royal Meenakshi Mall", "Bannerghatta, Bangalore", 200));
            theatreRepository.saveAll(theatres);

            // 4. Seed Showtimes
            List<Movie> allMovies = movieRepository.findAll();
            List<Theatre> allTheatres = theatreRepository.findAll();

            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);

            for (Movie movie : allMovies) {
                // Removed the "Bangalore" filter to generate showtimes for all movies
                for (Theatre theatre : allTheatres) {
                    // Create Morning (10:30), Evening (18:45), and Night (22:15) shows
                    createShowtime(movie, theatre, tomorrow.withHour(10).withMinute(30));
                    createShowtime(movie, theatre, tomorrow.withHour(18).withMinute(45));
                    createShowtime(movie, theatre, tomorrow.withHour(22).withMinute(15));
                }
            }
            System.out.println("✅ Seed data successfully loaded.");
        }
    }


    private Movie createMovie(String title, String url, String genre, Double rating, Double price, String city, String desc, Double dur, String lang) {
        Movie m = new Movie();
        m.setTitle(title);
        m.setPosterUrl(url);
        m.setGenre(genre);
        m.setRating(rating);
        m.setPrice(price);
        m.setCity(city);
        m.setDescription(desc);
        m.setDuration(dur);
        m.setLanguage(lang);
        return m;
    }

    private Theatre createTheatre(String name, String address, int capacity) {
        Theatre t = new Theatre();
        t.setName(name);
        t.setAddress(address);
        t.setCity("Bangalore");
        t.setSeatingCapacity(capacity);
        return t;
    }

    private void createShowtime(Movie movie, Theatre theatre, LocalDateTime time) {
        Showtime s = new Showtime();
        s.setMovie(movie);
        s.setTheatre(theatre);
        s.setStartTime(time);
        s.setTotalSeats(theatre.getSeatingCapacity());
        s.setSeatsBooked(0);
        showtimeRepository.save(s);
    }
}
