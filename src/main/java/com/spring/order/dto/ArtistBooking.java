package com.spring.order.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class ArtistBooking implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private int userId;
    private Date orderDate;
    private int lunch;
    private int dinner;
    private int supper;

    public ArtistBooking() {
    }

    public ArtistBooking(int userId, String name, Date orderDate, int lunch, int dinner, int supper) {
        this.userId = userId;
        this.name = name;
        this.orderDate = orderDate;
        this.lunch = lunch;
        this.dinner = dinner;
        this.supper = supper;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public int getLunch() {
        return lunch;
    }

    public void setLunch(int lunch) {
        this.lunch = lunch;
    }

    public int getDinner() {
        return dinner;
    }

    public void setDinner(int dinner) {
        this.dinner = dinner;
    }

    public int getSupper() {
        return supper;
    }

    public void setSupper(int supper) {
        this.supper = supper;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
