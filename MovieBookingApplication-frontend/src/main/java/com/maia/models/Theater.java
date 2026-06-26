package com.maia.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Theater {

    private Long id;
    private String theaterName;
    private String theaterLocation;
    private  Integer theaterCapacity;
    private  String theaterScreenTime;
    private List<Show> show;

    public Theater() {}

    public Long getId() {
        return id;
    }

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

    public List<Show> getShow() {
        return show;
    }
    public void setShow(List<Show> show) {
        this.show = show;
    }
}
