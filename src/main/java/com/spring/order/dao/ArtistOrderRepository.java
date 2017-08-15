package com.spring.order.dao;


import com.spring.order.dto.ArtistBooking;
import com.spring.order.dto.DateCount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ArtistOrderRepository extends CrudRepository<ArtistBooking, Integer> {

    List<ArtistBooking> findByOrderDate(Date orderDate);

    List<ArtistBooking> findByUserIdAndOrderDateGreaterThanOrderByOrderDateDesc(Integer userId, Date orderDate);

    ArtistBooking findByUserIdAndOrderDate(Integer userId, Date orderDate);

    @Query("select new com.spring.order.dto.DateCount(a.orderDate ,sum(a.lunch),sum(a.dinner),sum(a.supper)) from ArtistBooking a where a.orderDate >= ?1  and  a.orderDate <=?2 group by a.orderDate order by  a.orderDate desc ")
    List<DateCount> getOrderCount(Date begin, Date end);

    @Modifying
    @Query("update ArtistBooking set signLunch=?3 where userId=?1 and orderDate=?2 ")
    int updateSignLunch(int userId, Date orderDate, int sign);

    @Modifying
    @Query("update ArtistBooking set signDinner=?3 where userId=?1 and orderDate=?2 ")
    int updateDinnerLunch(int userId, Date orderDate, int sign);

    @Modifying
    @Query("update ArtistBooking set signSupper=?3 where userId=?1 and orderDate=?2 ")
    int updateSupperLunch(int userId, Date orderDate, int sign);
}
