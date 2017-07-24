package com.spring.order;


import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface SignRepository extends CrudRepository<Sign, Integer> {
    List<Sign> findByUserNameAndDepartment(String userName, String department);
}
