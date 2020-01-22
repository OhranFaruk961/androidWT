package com.example.faruk.wt_travel_agency;

import android.widget.EditText;

public class Tour {

    private String destination;

    private String price;

    private String departure;

    private String returnDate;

    private String imge_url;

    public Tour(){};

    public Tour(String destination, String price, String departure, String returnDate, String imge_url) {
        this.destination = destination;
        this.price = price;
        this.departure = departure;
        this.returnDate = returnDate;
        this.imge_url = imge_url;
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

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getImge_url() {
        return imge_url;
    }

    public void setImge_url(String imge_url) {
        this.imge_url = imge_url;
    }
}
