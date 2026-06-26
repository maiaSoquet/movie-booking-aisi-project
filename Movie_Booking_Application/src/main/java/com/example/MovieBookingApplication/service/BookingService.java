package com.example.MovieBookingApplication.service;

import com.example.MovieBookingApplication.DTO.BookingDTO;
import com.example.MovieBookingApplication.entity.Booking;
import com.example.MovieBookingApplication.entity.BookingStatus;
import com.example.MovieBookingApplication.entity.Show;
import com.example.MovieBookingApplication.entity.User;
import com.example.MovieBookingApplication.repository.BookingRepository;
import com.example.MovieBookingApplication.repository.ShowRepository;
import com.example.MovieBookingApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserRepository userRepository;

    public Booking createBooking(BookingDTO bookingDTO) {
        Show show=showRepository.findById(bookingDTO.getShowId()) //bcz show got theater information
                .orElseThrow(()->new RuntimeException("Show Not Found"));

        if ((!isSeatsAvailable(show.getId(),bookingDTO.getNumberOfSeats()))){
            throw new RuntimeException("Not Enough Seats Are Available");
        }

        if(bookingDTO.getSeatNumbers().size()!=bookingDTO.getNumberOfSeats()){
            throw new RuntimeException("Seat Number and Number of Seat must match");
        }
        validateDuplicateSeats(show.getId(),bookingDTO.getSeatNumbers());

        User user=userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(()-> new RuntimeException("User Not Found"));

        Booking booking=new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setNumberOfSeats(bookingDTO.getNumberOfSeats());
        booking.setSeatNumbers(bookingDTO.getSeatNumbers());
        booking.setPrice(calculateTotalAmount(show.getPrice(),bookingDTO.getNumberOfSeats()));
        booking.setBookingTime(LocalDateTime.now());
        booking.setBookingStatus(BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    private Double calculateTotalAmount(Double price, Integer numberOfSeats) {
        return price*numberOfSeats;
    }

    private void validateDuplicateSeats(Long showid, List<String> seatNumbers) {
        Show show=showRepository.findById(showid) //bcz show got theater information
                .orElseThrow(()->new RuntimeException("Show Not Found"));

        Set<String> occupiesSeats=show.getBookings()
                .stream().filter(b->b.getBookingStatus()!=BookingStatus.CANCELED)
                .flatMap(b->b.getSeatNumbers().stream())
                .collect(Collectors.toSet());

        List<String> duplicateSeats=seatNumbers.stream()
                .filter(occupiesSeats::contains)
                .collect(Collectors.toList());

        if ((!duplicateSeats.isEmpty())){
            throw new RuntimeException("Seats Are Already Booked");
        }
    }

    public boolean isSeatsAvailable(Long showid,Integer numberOfSeats){
        Show show=showRepository.findById(showid) //bcz show got theater information
                .orElseThrow(()->new RuntimeException("Show Not Found"));

        int bookedSeats=show.getBookings().stream()
                .filter(booking -> booking.getBookingStatus() != BookingStatus.CANCELED)
                .mapToInt(Booking::getNumberOfSeats)
                .sum();
        return show.getTheater().getTheaterCapacity()-bookedSeats>=numberOfSeats;

    }

    public List<Booking> getUserBookings(Long userid) {
        return bookingRepository.findByUserId(userid);
    }

    public List<Booking> getShowBookings(Long showid) {
        return bookingRepository.findByShowId(showid);
    }

    public Booking confirmBooking(Long bookingid) {
        Booking booking=bookingRepository.findById(bookingid)
                .orElseThrow(()->new RuntimeException("Booking Not Found"));
        if(booking.getBookingStatus()!=BookingStatus.PENDING){
            throw  new RuntimeException("Booking is not in pending state");
        }
        //payment Process
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);

      }

    public Booking cancelBooking(Long bookingid) {
        Booking booking=bookingRepository.findById(bookingid)
                .orElseThrow(()->new RuntimeException("Booking Not Found"));

        validateCancelation(booking);
        booking.setBookingStatus(BookingStatus.CANCELED);
        return bookingRepository.save(booking);
    }

    private void validateCancelation(Booking booking) {
         LocalDateTime showtime=booking.getShow().getShowTime();
         LocalDateTime deadlineTime=showtime.minusHours(2);

         if (LocalDateTime.now().isAfter(deadlineTime)){
             throw new RuntimeException("Cannot cancel Booking");
         }
         if (booking.getBookingStatus()==BookingStatus.CANCELED){
             throw new RuntimeException("Booking is Already Being Cancelled");
         }
    }

    public List<Booking> getBookingStatus(BookingStatus bookingStatus) {
        return bookingRepository.getByBookingStatus(bookingStatus);
    }
}
