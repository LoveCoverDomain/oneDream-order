package com.spring.order.service;

import com.spring.order.dao.ArtistOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by lichundong on 2017/8/15.
 */
@Service
public class ArtistOrderService {
    @Autowired
    private ArtistOrderRepository artistOrderRepository;


    @Transactional
    public int signLunch(int userId, Date orderDate, int sign) {
        return artistOrderRepository.updateSignLunch(userId, orderDate, sign);
    }

    @Transactional
    public int signDinner(int userId, Date orderDate, int sign) {
        return artistOrderRepository.updateDinnerLunch(userId, orderDate, sign);
    }

    @Transactional
    public int signSupper(int userId, Date orderDate, int sign) {
        return artistOrderRepository.updateSupperLunch(userId, orderDate, sign);
    }
}
