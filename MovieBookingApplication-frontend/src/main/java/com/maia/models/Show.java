package com.maia.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Show {

    private Long id;
    private LocalDateTime showTime;
    private Double price;
    private Movie movie;
    private Theater theater;
    private List<Booking> bookings;

    public Show() {}

    public Long getId() {
        return id;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }
    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    public Movie getMovie() {
        return movie;
    }
    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Theater getTheater() {
        return theater;
    }
    public void setTheater(Theater theater) {
        this.theater = theater;
    }

    public List<Booking> getBookings() {
        return bookings;
    }
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
