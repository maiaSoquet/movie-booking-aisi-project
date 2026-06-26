package com.example.MovieBookingApplication.DTO;

import lombok.Data;

@Data
public class TheaterDTO {
    private String theaterName;
    private String theaterLocation;
    private  Integer theaterCapacity;
    private  String theaterScreenTime;

    public String getTheaterName() {
        return theaterName;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    public String getTheaterLocation() {
        return theaterLocation;
    }

    public void setTheaterLocation(String theaterLocation) {
        this.theaterLocation = theaterLocation;
    }

    public Integer getTheaterCapacity() {
        return theaterCapacity;
    }

    public void setTheaterCapacity(Integer theaterCapacity) {
        this.theaterCapacity = theaterCapacity;
    }

    public String getTheaterScreenTime() {
        return theaterScreenTime;
    }

    public void setTheaterScreenTime(String theaterScreenTime) {
        this.theaterScreenTime = theaterScreenTime;
    }
}
