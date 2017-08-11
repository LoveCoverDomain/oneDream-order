package com.spring.order.dto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Booking implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String userName;
    private String department;
    private Date orderDate;
    private int lunch;
    private int dinner;
    private int supper;

    public Booking() {
    }

    public Booking(String userName, String department, Date orderDate, int lunch, int dinner, int supper) {
        this.userName = userName;
        this.department = department;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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
}
