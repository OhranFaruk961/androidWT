package com.example.faruk.wt_travel_agency;

public class Tour {

    private String destination;

    private String price;

    private String departureDate;

    private String returnDate;

    private String image;

    public Tour(){}

    public Tour(String destination, String price, String departureDate, String returnDate, String image) {
        this.destination = destination;
        this.price = price;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
