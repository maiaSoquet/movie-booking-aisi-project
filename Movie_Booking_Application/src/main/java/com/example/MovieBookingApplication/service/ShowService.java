package com.example.MovieBookingApplication.service;

import com.example.MovieBookingApplication.DTO.ShowDTO;
import com.example.MovieBookingApplication.entity.Booking;
import com.example.MovieBookingApplication.entity.Movie;
import com.example.MovieBookingApplication.entity.Show;
import com.example.MovieBookingApplication.entity.Theater;
import com.example.MovieBookingApplication.repository.MovieRepository;
import com.example.MovieBookingApplication.repository.ShowRepository;
import com.example.MovieBookingApplication.repository.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowService {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    TheaterRepository theaterRepository;

    public Show createShow(ShowDTO showDTO) {
        Movie movie = movieRepository.findById(showDTO.getMovieId())
                .orElseThrow(() -> new RuntimeException("No Movie Found"));

        Theater theater= theaterRepository.findById(showDTO.getTheaterId())
                .orElseThrow(()->new RuntimeException("No Theater Found"));

        Show show=new Show();
        show.setShowTime(showDTO.getShowTime());
        show.setPrice(showDTO.getPrice());
        show.setMovie(movie);
        show.setTheater(theater);

       return showRepository.save(show);
    }

    public List<Show> getAllShows() {
        return  showRepository.findAll();
    }

    public List<Show> getShowsByMovie(Long movieid) {
        Optional<List<Show>> showListBox = showRepository.findByMovieId(movieid);
        if (showListBox.isPresent()){
            return showListBox.get();
        }else throw new RuntimeException("No Shows Available for Movie");
    }

    public List<Show> getShowsByTheater(Long theaterid) {
        Optional<List<Show>> showListBox = showRepository.findByTheaterId(theaterid);
        if (showListBox.isPresent()){
            return showListBox.get();
        }else throw new RuntimeException("No Theater Available for Theater");
    }

    public Show updateShow(Long id, ShowDTO showDTO) {
        Show show=showRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("No show Available For id"));

        Movie movie = movieRepository.findById(showDTO.getMovieId())
                .orElseThrow(() -> new RuntimeException("No Movie Found"));

        Theater theater= theaterRepository.findById(showDTO.getTheaterId())
                .orElseThrow(()->new RuntimeException("No Theater Found"));

        show.setShowTime(showDTO.getShowTime());
        show.setPrice(showDTO.getPrice());
        show.setMovie(movie);
        show.setTheater(theater);

        return showRepository.save(show);
    }

    public void  deleteShow(Long id) {
        if (!showRepository.existsById(id)){
            throw  new RuntimeException("No Show Available for the id"+id);
        }
        List<Booking>bookings=showRepository.findById(id).get().getBookings();
        if (!bookings.isEmpty()){
            throw new RuntimeException("Can't Delete Show With Existing Bookings");
        }

        showRepository.deleteById(id);

    }
}
