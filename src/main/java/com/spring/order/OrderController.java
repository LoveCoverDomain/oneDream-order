package com.spring.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by chundong.lcd on 2017/7/15.
 */
@Controller
public class OrderController {
    private static final String cookieName_userName = "userName";
    private static final String cookieName_department = "department";

    @Autowired
    private OrderService orderService;
    @Autowired
    private SignService signService;

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public String index(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {
        String userName = getParam(httpRequest, cookieName_userName);
        String department = getParam(httpRequest, cookieName_department);

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(department)) {
            return "login";
        }

        String tomorrowStr = tomorrow();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
        List<Booking> bookings1 = orderService.getOrdersByDate(sdf.parse(tomorrowStr));

        List<Booking> bookings = orderService.getByUserName(userName, department).stream()
                .filter(p -> p.getOrderDate().after(new Date()))
                .collect(Collectors.toList());

        int lunchCount = 0;
        int dinnerCount = 0;

        for (Booking booking : bookings1) {
            lunchCount += booking.getLunch();
            dinnerCount += booking.getDinner();
        }

        model.addAttribute("bookings", bookings);

        model.addAttribute("tomorrow", tomorrowStr);

        model.addAttribute("userCount", bookings1.size());

        model.addAttribute("lunchCount", lunchCount);

        model.addAttribute("dinnerCount", dinnerCount);

        model.addAttribute("tomorrowUser", bookings1);


        return "order";
    }

    @RequestMapping(value = "/orderDo", method = RequestMethod.POST)
    public String order(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟

        String orderDate = httpRequest.getParameter("orderDate");
        if (orderDate == null || sdf.parse(orderDate).before(sdf.parse(tomorrow()))) {
            model.addAttribute("text", "只有明天及以后可点餐");
            model.addAttribute("button", "继续点餐");

            return "success";
        }

        String lunchStr = httpRequest.getParameter("lunch");
        int lunch = Integer.valueOf(lunchStr == null ? "0" : lunchStr);

        String dinnerStr = httpRequest.getParameter("dinner");
        int dinner = Integer.valueOf(dinnerStr == null ? "0" : dinnerStr);

        String userName = getParam(httpRequest, cookieName_userName);
        String department = getParam(httpRequest, cookieName_department);


        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(department)) {
            return "login";
        }

        if (isTodayAfterClock(16) && tomorrow().equals(orderDate)) {
            model.addAttribute("text", "时间超过了下午4点，不能再点餐");
            model.addAttribute("button", "继续点餐");

            return "success";
        }

        Booking bookingDb = orderService.getByUserNameAndOrderDate(userName, department, sdf.parse(orderDate));

        try {
            if (bookingDb != null) {
                bookingDb.setLunch(lunch);
                bookingDb.setDinner(dinner);
                orderService.updateOrder(bookingDb);
            } else {
                Booking booking = new Booking(userName, department, sdf.parse(orderDate), lunch, dinner);
                orderService.createOrder(booking);
            }
        } catch (Exception e) {
            if (e.getMessage().contains("uk_user_orderdate")) {
                model.addAttribute("text", "订餐成功");
                model.addAttribute("button", "返回,点击\"查看\"可查阅订餐结果");
                return "success";
            }
            throw e;
        }

        model.addAttribute("text", "订餐成功");
        model.addAttribute("button", "返回,点击\"查看\"可查阅订餐结果");

        return "success";
    }

    @RequestMapping(value = "/loginDo", method = RequestMethod.POST)
    public String login(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {
        String userName = httpRequest.getParameter("userName");
        String department = httpRequest.getParameter("department");

        addCookie(httpServletResponse, cookieName_userName, userName);
        addCookie(httpServletResponse, cookieName_department, department);


        model.addAttribute("text", "注册成功");
        model.addAttribute("button", "开始点餐");

        model.addAttribute("userName", userName);
        model.addAttribute("department", department);

        return "success";
    }

    @RequestMapping(value = "/sign", method = RequestMethod.GET)
    public String sign(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {
        String userName = getParam(httpRequest, cookieName_userName);
        String department = getParam(httpRequest, cookieName_department);

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(department)) {
            return "login";
        }

        int hour = getNowOfHour();
        int lunch = 0;
        int dinner = 0;
        String text = "";
        if (hour >= 11 && hour <= 14) {
            lunch = 1;
            text = "午餐";
        }
        if (hour > 16 && hour <= 20) {
            dinner = 1;
            text = "晚餐";
        }

        Sign sign = new Sign(userName, department, new Date(), lunch, dinner);
        signService.create(sign);

        model.addAttribute("text", text + "签到成功");
        model.addAttribute("button", "返回,点击\"查看\"可查阅订餐结果");

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

    /**
     * 根据名字获取cookie
     *
     * @param request
     * @param name    cookie名字
     * @return
     */
    public Cookie getCookieByName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = ReadCookieMap(request);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = (Cookie) cookieMap.get(name);
            return cookie;
        } else {
            return null;
        }
    }

    /**
     * 将cookie封装到Map里面
     *
     * @param request
     * @return
     */
    private Map<String, Cookie> ReadCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

    private String tomorrow() {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }

    private int getNowOfHour() {
        Date now = new Date();

        int hour = now.getHours();

        return hour;
    }

    private boolean isTodayAfterClock(int clock) {

        Date date = new Date();//取时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);

        Calendar calendar = new GregorianCalendar();
        try {
            calendar.setTime(formatter.parse(dateString));
            calendar.set(Calendar.HOUR, clock);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                return true;
            }
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getParam(HttpServletRequest httpRequest, String key) {
        String userName = httpRequest.getParameter(key);
        if (userName != null) {
            return userName;
        }

        Cookie cookieUserName = getCookieByName(httpRequest, key);
        if (cookieUserName != null) {
            return URLDecoder.decode(cookieUserName.getValue());
        }
        return null;
    }

}
