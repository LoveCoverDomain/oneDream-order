package com.spring.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by lichundong on 2017/7/26.
 */
@Controller
public class InfoController {


    @Autowired
    private OrderService orderService;
    @Autowired
    private SignService signService;


    @RequestMapping(value = "/loginDo", method = RequestMethod.POST)
    public String login(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {
        String userName = httpRequest.getParameter("userName");
        String department = httpRequest.getParameter("department");

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(department)) {
            model.addAttribute("text", "用户名和部门不能为空");
            model.addAttribute("button", "返回");

            return "success";
        }

        String userNameCookie = Util.getParamByCookie(httpRequest, Util.cookieName_userName);
        String departmentCookie = Util.getParamByCookie(httpRequest, Util.cookieName_department);

        if (!StringUtils.isEmpty(userNameCookie) && !StringUtils.isEmpty(departmentCookie)) {
            List<Booking> bookings = orderService.getByUserName(userNameCookie, departmentCookie);

            for (Booking booking : bookings) {
                booking.setDepartment(department);
                booking.setUserName(userName);
                orderService.updateOrder(booking);
            }

            List<Sign> signs = signService.getByUserName(userNameCookie, departmentCookie);

            for (Sign sign : signs) {
                sign.setDepartment(department);
                sign.setUserName(userName);
                signService.update(sign);
            }
        }

        addCookie(httpServletResponse, Util.cookieName_userName, userName);
        addCookie(httpServletResponse, Util.cookieName_department, department);


        model.addAttribute("text", "注册成功");
        model.addAttribute("button", "开始点餐");

        return "success";
    }

    @RequestMapping(value = "/updateDo", method = RequestMethod.POST)
    public String update(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {
        String userName = httpRequest.getParameter("userName");
        String department = httpRequest.getParameter("department");

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(department)) {
            model.addAttribute("text", "用户名和部门不能为空");
            model.addAttribute("button", "返回");
            return "success";
        }

        String userNameCookie = Util.getParamByCookie(httpRequest, Util.cookieName_userName);
        String departmentCookie = Util.getParamByCookie(httpRequest, Util.cookieName_department);

        if (!StringUtils.isEmpty(userNameCookie) && !StringUtils.isEmpty(departmentCookie)) {
            if (userNameCookie.equals(userName) && departmentCookie.equals(department)) {
                model.addAttribute("text", "个人信息没有改变哦");
                model.addAttribute("button", "返回");
                return "success";
            }

            List<Booking> bookings = orderService.getByUserName(userNameCookie, departmentCookie);

            for (Booking booking : bookings) {
                booking.setDepartment(department);
                booking.setUserName(userName);
                orderService.updateOrder(booking);
            }

            List<Sign> signs = signService.getByUserName(userNameCookie, departmentCookie);

            for (Sign sign : signs) {
                sign.setDepartment(department);
                sign.setUserName(userName);
                signService.update(sign);
            }
        }

        addCookie(httpServletResponse, Util.cookieName_userName, userName);
        addCookie(httpServletResponse, Util.cookieName_department, department);


        model.addAttribute("text", "个人信息修改成功");
        model.addAttribute("button", "返回");

        return "success";
    }

    /**
     * 添加cookie
     *
     * @param response
     * @param name
     * @param value
     */
    public void addCookie(HttpServletResponse response, String name, String value) {
        String valueEn = URLEncoder.encode(value.trim());
        Cookie cookie = new Cookie(name.trim(), valueEn);
        cookie.setMaxAge(10 * 366 * 24 * 60 * 60);// 设置为30min
        cookie.setPath("/");
        response.addCookie(cookie);
    }


}
