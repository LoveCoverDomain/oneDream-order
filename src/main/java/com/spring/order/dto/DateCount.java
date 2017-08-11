package com.spring.order.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by lichundong on 2017/7/24.
 */
@Entity
public class DateCount implements Serializable {
    @Id
    private Date date;
    private Long lunchCount;
    private Long dinnerCount;
    private Long supperCount;
    private Long signLunchCount;
    private Long signDinnerCount;
    private Long signSupperCount;

    private Long count;

    public DateCount() {
    }

    public DateCount(Date date, Long lunchCount, Long dinnerCount, Long supperCount) {
        this.date = date;
        this.lunchCount = lunchCount;
        this.dinnerCount = dinnerCount;
        this.supperCount = supperCount;
    }

    public DateCount(Date date, Long count) {
        this.date = date;
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getLunchCount() {
        return lunchCount;
    }

    public void setLunchCount(Long lunchCount) {
        this.lunchCount = lunchCount;
    }

    public Long getDinnerCount() {
        return dinnerCount;
    }

    public void setDinnerCount(Long dinnerCount) {
        this.dinnerCount = dinnerCount;
    }

    public Long getSignLunchCount() {
        return signLunchCount;
    }

    public void setSignLunchCount(Long signLunchCount) {
        this.signLunchCount = signLunchCount;
    }

    public Long getSignDinnerCount() {
        return signDinnerCount;
    }

    public void setSignDinnerCount(Long signDinnerCount) {
        this.signDinnerCount = signDinnerCount;
    }

    public Long getSupperCount() {
        return supperCount;
    }

    public void setSupperCount(Long supperCount) {
        this.supperCount = supperCount;
    }

    public Long getSignSupperCount() {
        return signSupperCount;
    }

    public void setSignSupperCount(Long signSupperCount) {
        this.signSupperCount = signSupperCount;
    }
}
