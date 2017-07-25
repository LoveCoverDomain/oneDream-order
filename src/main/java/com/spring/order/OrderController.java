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
import java.util.function.Function;
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
        String userName = getParamByCookie(httpRequest, cookieName_userName);
        String department = getParamByCookie(httpRequest, cookieName_department);

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(department)) {
            return "login";
        }

        String tomorrowStr = tomorrow();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟

        List<Booking> bookings1 = orderService.getOrdersByDate(sdf.parse(tomorrowStr));

        List<Booking> bookings = orderService.getByUserName(userName, department, sdf.parse(today()));

        int lunchCount = 0;
        int dinnerCount = 0;

        for (Booking booking : bookings1) {
            lunchCount += booking.getLunch();
            dinnerCount += booking.getDinner();
        }

        if (dinnerCount > 0) {
            dinnerCount = dinnerCount + 3;
        }

        model.addAttribute("bookings", bookings);

        model.addAttribute("tomorrow", tomorrowStr);

        model.addAttribute("userCount", bookings1.size());

        model.addAttribute("lunchCount", lunchCount);

        model.addAttribute("dinnerCount", dinnerCount);

        model.addAttribute("tomorrowUser", bookings1);

        model.addAttribute("userName", userName);

        model.addAttribute("department", department);

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
            model.addAttribute("text", "时间超过了下午4点，不能再点餐了");
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

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(department)) {
            model.addAttribute("text", "用户名和部门不能为空");
            model.addAttribute("button", "返回");

            return "success";
        }

        String userNameCookie = getParamByCookie(httpRequest, cookieName_userName);
        String departmentCookie = getParamByCookie(httpRequest, cookieName_department);

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

        addCookie(httpServletResponse, cookieName_userName, userName);
        addCookie(httpServletResponse, cookieName_department, department);


        model.addAttribute("text", "注册成功");
        model.addAttribute("button", "开始点餐");

        return "success";
    }

    @RequestMapping(value = "/sign", method = RequestMethod.GET)
    public String sign(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {
        String userName = getParamByCookie(httpRequest, cookieName_userName);
        String department = getParamByCookie(httpRequest, cookieName_department);

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(department)) {
            return "login";
        }

        int hour = getNowOfHour();
        int lunch = 0;
        int dinner = 0;
        String text = "";
        if (hour >= 10 && hour <= 13) {
            lunch = 1;
            text = "午餐";
        }
        if (hour >= 16 && hour <= 20) {
            dinner = 1;
            text = "晚餐";
        }

        if (lunch == 0 && dinner == 0) {
            text = " 不在用餐时间，无法签到。午餐：10点-13点 晚餐：16点-20点";
        } else {
            boolean signed = isSign(userName, department, lunch, dinner);

            if (signed) {
                text = text + " 已签到!";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                Sign sign = new Sign(userName, department, sdf.parse(today()), lunch, dinner);
                signService.create(sign);
                text = text + " 签到成功!";
            }
        }


        model.addAttribute("text", text);
        model.addAttribute("userName", userName);
        model.addAttribute("department", department);

        return "sign";
    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    private String statistics(Model model) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟

        List<DateCount> result = orderService.getCount(sdf.parse(today()));

        Map<Date, Long> signLunchMap = signService.getLunchCount(sdf.parse(today())).stream()
                .collect(Collectors.toMap(DateCount::getDate, DateCount::getCount));

        Map<Date, Long> signDinnerMap = signService.getDinnerCount(sdf.parse(today())).stream()
                .collect(Collectors.toMap(DateCount::getDate, DateCount::getCount));

        for (DateCount dateCount : result) {
            Long signLunch = signLunchMap.get(dateCount.getDate());
            Long signDinner = signDinnerMap.get(dateCount.getDate());

            if (dateCount.getDinnerCount() > 0) {
                dateCount.setDinnerCount(dateCount.getDinnerCount() + 3);
            }

            dateCount.setSignLunchCount(signLunch == null ? 0 : signLunch);
            dateCount.setSignDinnerCount(signDinner == null ? 0 : signDinner + 3);
        }

        model.addAttribute("result", result);

        return "statistics";
    }


    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    private String detail(String date, Model model) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
        Date d = sdf.parse(date);

        Set<String> set = new HashSet();
        List<Booking> bookings = orderService.getOrdersByDate(d);
        List<Sign> lunchSign = signService.getLunchBySignTime(d);
        List<Sign> dinnerSign = signService.getDinnerBySignTime(d);

        List<String> lunchSignKey = lunchSign.stream().map(p -> p.getUserName() + "-" + p.getDepartment()).collect(Collectors.toList());
        List<String> dinnerSignKey = dinnerSign.stream().map(p -> p.getUserName() + "-" + p.getDepartment()).collect(Collectors.toList());


        List<DateDetailCount> result = new ArrayList<>();
        bookings.stream().forEach(p -> {
            String key = p.getUserName() + "-" + p.getDepartment();
            if (!set.contains(key)) {
                DateDetailCount detail = new DateDetailCount();
                detail.setDate(d);
                detail.setUserName(p.getUserName());
                detail.setDepartment(p.getDepartment());
                detail.setLunchCount(Long.valueOf(p.getLunch()));
                detail.setDinnerCount(Long.valueOf(p.getDinner()));
                detail.setSignLunchCount(lunchSignKey.contains(key) ? 1L : 0L);
                detail.setSignDinnerCount(dinnerSignKey.contains(key) ? 1L : 0);

                set.add(key);
                result.add(detail);
            }
        });

        lunchSign.stream().forEach(p -> {
            String key = p.getUserName() + "-" + p.getDepartment();
            if (!set.contains(key)) {
                DateDetailCount detail = new DateDetailCount();
                detail.setDate(d);
                detail.setUserName(p.getUserName());
                detail.setDepartment(p.getDepartment());
                detail.setLunchCount(0L);
                detail.setDinnerCount(0L);
                detail.setSignLunchCount(1L);
                detail.setSignDinnerCount(dinnerSignKey.contains(key) ? 1L : 0);

                set.add(key);
                result.add(detail);
            }
        });

        dinnerSign.stream().forEach(p -> {
            String key = p.getUserName() + "-" + p.getDepartment();
            if (!set.contains(key)) {
                DateDetailCount detail = new DateDetailCount();
                detail.setDate(d);
                detail.setUserName(p.getUserName());
                detail.setDepartment(p.getDepartment());
                detail.setLunchCount(0L);
                detail.setDinnerCount(0L);
                detail.setSignLunchCount(lunchSignKey.contains(key) ? 1L : 0L);
                detail.setSignDinnerCount(1L);

                set.add(key);
                result.add(detail);
            }
        });

        model.addAttribute("result", result);

        return "detail";
    }

    private boolean isSign(String userName, String department, int lunch, int dinner) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
        boolean signed = false;
        List<Sign> signs = signService.getByUserNameAndSignTime(userName, department, sdf.parse(today()));
        for (Sign sign : signs) {
            if (sign.getLunch() == lunch && sign.getDinner() == dinner) {
                signed = true;
            }
        }
        return signed;
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

    private String today() {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
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

        return getParamByCookie(httpRequest, key);
    }

    private String getParamByCookie(HttpServletRequest httpRequest, String key) {
        Cookie cookieUserName = getCookieByName(httpRequest, key);
        if (cookieUserName != null) {
            return URLDecoder.decode(cookieUserName.getValue());
        }
        return null;
    }

}
