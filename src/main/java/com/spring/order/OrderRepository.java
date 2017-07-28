package com.spring.order;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderRepository extends CrudRepository<Booking, Integer> {

    List<Booking> findByOrderDate(Date orderDate);


    List<Booking> findByUserNameAndDepartmentOrderByOrderDateAsc(String userName, String department);

    List<Booking> findByUserNameAndDepartmentAndOrderDateGreaterThanOrderByOrderDateDesc(String userName, String department, Date orderDate);

    Booking findByUserNameAndDepartmentAndOrderDate(String userName, String department, Date orderDate);

    @Query("select new com.spring.order.DateCount(a.orderDate ,sum(a.lunch),sum(a.dinner),sum(a.supper)) from Booking a where a.orderDate >= '2017-07-24 00:00:00'  and  a.orderDate <=?1 group by a.orderDate order by  a.orderDate desc ")
    List<DateCount> getOrderCount(Date orderDate);

    @Query("select new com.spring.order.DateCount(a.orderDate ,sum(a.lunch),sum(a.dinner),sum(a.supper)) from Booking a where a.orderDate >= ?1 group by a.orderDate order by  a.orderDate asc ")
    List<DateCount> getCountByOrder(Date orderDate);
}
