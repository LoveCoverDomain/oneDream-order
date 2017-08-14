package com.spring.order.dao;


import com.spring.order.dto.ArtistBooking;
import com.spring.order.dto.DateCount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ArtistOrderRepository extends CrudRepository<ArtistBooking, Integer> {

    List<ArtistBooking> findByOrderDate(Date orderDate);

    List<ArtistBooking> findByUserIdOrderByOrderDateAsc(Integer userId);

    List<ArtistBooking> findByUserIdAndOrderDateGreaterThanOrderByOrderDateDesc(Integer userId, Date orderDate);

    ArtistBooking findByUserIdAndOrderDate(Integer userId, Date orderDate);

    @Query("select new com.spring.order.dto.DateCount(a.orderDate ,sum(a.lunch),sum(a.dinner),sum(a.supper)) from Booking a where a.orderDate >= ?1  and  a.orderDate <=?2 group by a.orderDate order by  a.orderDate desc ")
    List<DateCount> getOrderCount(Date begin, Date end);

    @Query("select new com.spring.order.dto.DateCount(a.orderDate ,sum(a.lunch),sum(a.dinner),sum(a.supper)) from Booking a where a.orderDate >= ?1 group by a.orderDate order by  a.orderDate asc ")
    List<DateCount> getCountByOrder(Date orderDate);
}
