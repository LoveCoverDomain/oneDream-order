package com.spring.order;

/**
 * Created by lichundong on 2017/7/24.
 */
public class DateDetailCount extends DateCount {
    private String userName;
    private String department;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
