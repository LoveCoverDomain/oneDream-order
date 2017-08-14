package com.spring.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lichundong on 2017/7/17.
 */
@Service
public class SignService {
    @Autowired
    private SignRepository signRepository;

    public void create(Sign sign) {
        signRepository.save(sign);
    }
}
