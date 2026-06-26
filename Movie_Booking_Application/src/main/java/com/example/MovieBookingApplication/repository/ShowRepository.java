package com.example.MovieBookingApplication.repository;

import com.example.MovieBookingApplication.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show,Long> {

    // it will search "movieid" in Show entity
    Optional<List<Show>> findByMovieId(Long movieid);

    Optional<List<Show>> findByTheaterId(Long theaterid);
}
