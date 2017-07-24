package com.spring.order;


import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends CrudRepository<Booking, Integer> {
    List<Booking> findByOrderDate(Date orderDate);


    List<Booking> findByUserNameAndDepartmentOrderByOrderDateAsc(String userName, String department);

    List<Booking> findByUserNameAndDepartmentAndOrderDateGreaterThanOrderByOrderDateDesc(String userName, String department,Date orderDate);

    Booking findByUserNameAndDepartmentAndOrderDate(String userName, String department, Date orderDate);
}
