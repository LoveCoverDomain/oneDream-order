package com.spring.order.service;

import com.spring.order.dao.SignRepository;
import com.spring.order.dto.DateCount;
import com.spring.order.dto.Sign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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

    public List<Sign> getByUserNameAndSignTime(String userName, String department, Date signTime) {
        List<Sign> signs = new ArrayList<>();
        signRepository.findByUserNameAndDepartmentAndSignTime(userName, department, signTime).forEach(signs::add);
        return signs;
    }

    public List<Sign> getLunchBySignTime(Date signTime) {
        List<Sign> signs = new ArrayList<>();
        signRepository.findBySignTimeAndLunch(signTime, 1).forEach(signs::add);
        return signs;
    }

    public List<Sign> getDinnerBySignTime(Date signTime) {
        List<Sign> signs = new ArrayList<>();
        signRepository.findBySignTimeAndDinner(signTime, 1).forEach(signs::add);
        return signs;
    }

    public List<Sign> getSupperBySignTime(Date signTime) {
        List<Sign> signs = new ArrayList<>();
        signRepository.findBySignTimeAndSupper(signTime, 1).forEach(signs::add);
        return signs;
    }

    public List<DateCount> getLunchCount(Date begin,Date end) {
        return signRepository.getLunchCount(begin,end);
    }

    public List<DateCount> getDinnerCount(Date begin,Date end) {
        return signRepository.getDinnerCount(begin,end);
    }

    public List<DateCount> getSupperCount(Date begin,Date end) {
        return signRepository.getSupperCount(begin,end);
    }

}
