package com.example.MovieBookingApplication.service;

import com.example.MovieBookingApplication.DTO.MovieDTO;
import com.example.MovieBookingApplication.entity.Movie;

import com.example.MovieBookingApplication.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    
    @Autowired
    MovieRepository movieRepository;

    public Movie addMovie(MovieDTO movieDTO) {
        Movie movie = new Movie();
        movie.setName(movieDTO.getName());
        movie.setDescription(movieDTO.getDescription());
        movie.setGenre(movieDTO.getGenre());
        movie.setReleaseDate(movieDTO.getReleaseDate());
        movie.setDuration(movieDTO.getDuration());
        movie.setLanguage(movieDTO.getLanguage());
       return movieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
       return movieRepository.findAll();
    }

    public List<Movie> getMoviesByGenre(String genre) {
        Optional<List<Movie>> listOfMovieBox= movieRepository.findByGenre(genre);

        if (listOfMovieBox.isPresent()){
            return listOfMovieBox.get();
        }else throw new RuntimeException("No Movie Found for Genre"+ genre);
    }

    public List<Movie> getMoviesByLanguage(String language) {
        Optional<List<Movie>> listOfMovieBox= movieRepository.findByLanguage(language);

        if (listOfMovieBox.isPresent()){
            return listOfMovieBox.get();
        }else throw new RuntimeException("No Movie Found for Language"+ language);

    }

    public Movie getMoviesByTitle(String title) {
        Optional<Movie> MovieBox= movieRepository.findByName(title);

        if (MovieBox.isPresent()){
            return MovieBox.get();
        }else throw new RuntimeException("No Movie Found for Title"+ title);
    }

    public Movie updateMovie(Long id, MovieDTO movieDTO) {
        Movie movie =movieRepository.findById(id).orElseThrow(()-> new RuntimeException("No Movie Found For id"+ id));

        movie.setName(movieDTO.getName());
        movie.setDescription(movieDTO.getDescription());
        movie.setGenre(movieDTO.getGenre());
        movie.setReleaseDate(movieDTO.getReleaseDate());
        movie.setDuration(movieDTO.getDuration());
        movie.setLanguage(movieDTO.getLanguage());
        return movieRepository.save(movie);
    }

    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}
