package com.spring.order.dao;


import com.spring.order.dto.DateCount;
import com.spring.order.dto.Sign;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface SignRepository extends CrudRepository<Sign, Integer> {
    List<Sign> findByUserNameAndDepartment(String userName, String department);

    List<Sign> findByUserNameAndDepartmentAndSignTime(String userName, String department, Date signTime);

    List<Sign> findBySignTimeAndLunch(Date signTime, int lunch);

    List<Sign> findBySignTimeAndDinner(Date signTime, int dinner);

    List<Sign> findBySignTimeAndSupper(Date signTime, int supper);


    @Query("select new com.spring.order.dto.DateCount(a.signTime ,sum(a.dinner)) from Sign a where a.signTime >= '2017-07-24 00:00:00' and a.signTime <= ?1 and dinner=1 group by a.signTime order by a.signTime desc")
    List<DateCount> getDinnerCount(Date signTime);


    @Query("select new com.spring.order.dto.DateCount(a.signTime ,sum(a.lunch)) from Sign a where a.signTime >= '2017-07-24 00:00:00' and a.signTime <= ?1 and lunch=1 group by a.signTime order by a.signTime desc ")
    List<DateCount> getLunchCount(Date signTime);

    @Query("select new com.spring.order.dto.DateCount(a.signTime ,sum(a.supper)) from Sign a where a.signTime >= '2017-07-24 00:00:00' and a.signTime <= ?1 and supper=1 group by a.signTime order by a.signTime desc ")
    List<DateCount> getSupperCount(Date signTime);
}
