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
    private long phone;
    private String sex;
    private int userId;
    private Date orderDate;
    private int lunch;
    private int dinner;
    private int supper;
    private int signLunch;
    private int signDinner;
    private int signSupper;

    public ArtistBooking() {
    }

    public ArtistBooking(int userId, String name, long phone, String sex, Date orderDate, int lunch, int dinner, int supper) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.sex = sex;
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

    public int getSignLunch() {
        return signLunch;
    }

    public void setSignLunch(int signLunch) {
        this.signLunch = signLunch;
    }

    public int getSignDinner() {
        return signDinner;
    }

    public void setSignDinner(int signDinner) {
        this.signDinner = signDinner;
    }

    public int getSignSupper() {
        return signSupper;
    }

    public void setSignSupper(int signSupper) {
        this.signSupper = signSupper;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
