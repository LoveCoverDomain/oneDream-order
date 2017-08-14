package com.spring.order.dao;


import com.spring.order.dto.UserDTO;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserDTO, Integer> {
    UserDTO findByPhone(Long phone);
}
