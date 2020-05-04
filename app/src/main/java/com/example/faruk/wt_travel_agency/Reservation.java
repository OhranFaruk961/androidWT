package com.example.faruk.wt_travel_agency;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class Reservation {

    @Exclude
    private String id;
    private String destination;

    private String price;

    private String departureDate;

    private String returnDate;

    public Reservation(){}

    public Reservation(String destination, String price, String departureDate, String returnDate) {
        this.destination = destination;
        this.price = price;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
    }

    public <T extends Reservation> T withId(@NonNull final String id) {
        this.id = id;
        return (T) this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

}
