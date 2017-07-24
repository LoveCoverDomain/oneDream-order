package com.spring.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public void update(Sign sign) {
        signRepository.save(sign);
    }

    public List<Sign> getByUserName(String userName, String department) {
        List<Sign> signs = new ArrayList<>();
        signRepository.findByUserNameAndDepartment(userName, department).forEach(signs::add);
        return signs;
    }
}
