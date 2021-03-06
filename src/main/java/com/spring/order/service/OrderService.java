package com.spring.order.service;

import com.spring.order.dao.OrderRepository;
import com.spring.order.dto.Booking;
import com.spring.order.dto.DateCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Booking> getOrdersByDate(Date orderDate) {
        List<Booking> bookings = new ArrayList<>();
        orderRepository.findByOrderDate(orderDate).forEach(bookings::add);
        return bookings;
    }

    public List<Booking> getByUserName(String userName, String department) {
        List<Booking> bookings = new ArrayList<>();
        orderRepository.findByUserNameAndDepartmentOrderByOrderDateAsc(userName, department).forEach(bookings::add);
        return bookings;
    }

    public List<Booking> getByUserName(String userName, String department, Date orderDate) {
        List<Booking> bookings = new ArrayList<>();
        orderRepository.findByUserNameAndDepartmentAndOrderDateGreaterThanOrderByOrderDateDesc(userName, department, orderDate).forEach(bookings::add);
        return bookings;
    }

    public List<DateCount> getCount(Date begin,Date end) {
        return orderRepository.getOrderCount(begin,end);
    }

    public List<DateCount> getCountByOrder(Date orderDate) {
        return orderRepository.getCountByOrder(orderDate);
    }


    public Booking getByUserNameAndOrderDate(String userName, String department, Date orderDate) {
        return orderRepository.findByUserNameAndDepartmentAndOrderDate(userName, department, orderDate);
    }

    public void createOrder(Booking booking) {
        orderRepository.save(booking);
    }

    public void updateOrder(Booking booking) {
        orderRepository.save(booking);
    }

}
