package com.spring.order;

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
        orderRepository.findByUserNameAndDepartment(userName, department).forEach(bookings::add);
        return bookings;
    }

    public Booking getByUserNameAndOrderDate(String userName, String department, Date orderDate) {
        return orderRepository.findByUserNameAndDepartmentAndOrderDate(userName, department, orderDate);
    }


    public List<Booking> getAllOrders() {
        List<Booking> bookings = new ArrayList<>();
        orderRepository.findAll().forEach(bookings::add);
        return bookings;
    }

    public void createOrder(Booking booking) {
        orderRepository.save(booking);
    }

    public void updateOrder(Booking booking) {
        orderRepository.save(booking);
    }

    public void deleteOrder(int order_id) {
        orderRepository.delete(order_id);
    }
}
