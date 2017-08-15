package com.spring.order.controller;

import com.spring.order.TimeUtil;
import com.spring.order.dao.ArtistOrderRepository;
import com.spring.order.dto.Booking;
import com.spring.order.dto.DateCount;
import com.spring.order.dto.DateDetailCount;
import com.spring.order.dto.Sign;
import com.spring.order.service.OrderService;
import com.spring.order.service.SignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
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
    @Autowired
    private ArtistOrderRepository artistOrderRepository;

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public String index(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {
        String userName = getParamByCookie(httpRequest, cookieName_userName);
        String department = getParamByCookie(httpRequest, cookieName_department);

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(department)) {
            return "login";
        }

        String tomorrowStr = TimeUtil.tomorrow();

        String orderDateStr = TimeUtil.tomorrow();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟

        List<Booking> bookings = orderService.getByUserName(userName, department, sdf.parse(TimeUtil.yesterday()));

        List<Booking> bookings1 = orderService.getOrdersByDate(sdf.parse(tomorrowStr));

        List<DateCount> dateCounts = orderService.getCountByOrder(sdf.parse(tomorrowStr));

        List<DateCount> artistDateCounts = artistOrderRepository.getOrderCount(sdf.parse(tomorrowStr), TimeUtil.afterToday(20));

        Map<Date, DateCount> artistDateMap = artistDateCounts.stream().collect(Collectors.toMap(DateCount::getDate, Function.identity()
        ));

        dateCounts.stream().forEach(p -> {
            if (p.getDinnerCount() > 0) {
                Long dinnerCount = p.getDinnerCount() + 3;
                p.setDinnerCount(dinnerCount);
            }

            DateCount artist = artistDateMap.get(p.getDate());
            if (artist != null) {
                p.setLunchCount(p.getLunchCount() + artist.getLunchCount());
                p.setDinnerCount(p.getDinnerCount() + artist.getDinnerCount());
                p.setSupperCount(p.getSupperCount() + artist.getSupperCount());
            }

        });

        if (!CollectionUtils.isEmpty(bookings)) {
            Booking booking = bookings.get(0);

            Long orderDate = Math.max(sdf.parse(tomorrowStr).getTime(), sdf.parse(TimeUtil.tomorrow(booking.getOrderDate())).getTime());

            Date od = new Date(orderDate);

            orderDateStr = sdf.format(od);
        }

        model.addAttribute("orderDate", orderDateStr);

        model.addAttribute("bookings", bookings);

        model.addAttribute("tomorrow", tomorrowStr);

        model.addAttribute("userCount", dateCounts);

        model.addAttribute("tomorrowUser", bookings1);

        model.addAttribute("userName", userName);

        model.addAttribute("department", department);

        return "order";
    }

    @RequestMapping(value = "/orderDo", method = RequestMethod.POST)
    public String order(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟

        String orderDate = httpRequest.getParameter("orderDate");
        if (orderDate == null || sdf.parse(orderDate).before(sdf.parse(TimeUtil.tomorrow()))) {
            model.addAttribute("text", "只有明天及以后可点餐");
            model.addAttribute("button", "继续点餐");

            return "success";
        }

        String lunchStr = httpRequest.getParameter("lunch");
        int lunch = Integer.valueOf(lunchStr == null ? "0" : lunchStr);

        String dinnerStr = httpRequest.getParameter("dinner");
        int dinner = Integer.valueOf(dinnerStr == null ? "0" : dinnerStr);


        String supperStr = httpRequest.getParameter("supper");
        int supper = Integer.valueOf(supperStr == null ? "0" : supperStr);

        String userName = getParam(httpRequest, cookieName_userName);
        String department = getParam(httpRequest, cookieName_department);


        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(department)) {
            return "login";
        }

        if (TimeUtil.isTodayAfterClock(16, 30) && TimeUtil.tomorrow().equals(orderDate)) {
            model.addAttribute("text", "时间超过了下午4:30，不能再点餐了");
            model.addAttribute("button", "继续点餐");

            return "success";
        }

        Booking bookingDb = orderService.getByUserNameAndOrderDate(userName, department, sdf.parse(orderDate));

        try {
            if (bookingDb != null) {
                bookingDb.setLunch(lunch);
                bookingDb.setDinner(dinner);
                bookingDb.setSupper(supper);
                orderService.updateOrder(bookingDb);
            } else {
                Booking booking = new Booking(userName, department, sdf.parse(orderDate), lunch, dinner, supper);
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


    @RequestMapping(value = "/sign", method = RequestMethod.GET)
    public String sign(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {
        String userName = getParamByCookie(httpRequest, cookieName_userName);
        String department = getParamByCookie(httpRequest, cookieName_department);

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(department)) {
            return "login";
        }

        int hour = TimeUtil.getNowOfHour();
        int lunch = 0;
        int dinner = 0;
        int supper = 0;
        String text = "";
        if (hour >= 10 && hour <= 13) {
            lunch = 1;
            text = "午餐";
        }
        if (hour >= 16 && hour <= 19) {
            dinner = 1;
            text = "晚餐";
        }

        if (hour >= 21) {
            supper = 1;
            text = "夜宵";
        }


        if (lunch == 0 && dinner == 0 && supper == 0) {
            text = " 不在用餐时间，无法签到。午餐：10点-14点 晚餐：16点-20点 夜宵：21点-24点";
        } else {
            boolean signed = isSign(userName, department, lunch, dinner, supper);

            if (signed) {
                text = text + " 已签到!";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
                Sign sign = new Sign(userName, department, sdf.parse(TimeUtil.today()), lunch, dinner, supper);
                signService.create(sign);
                text = text + " 签到成功!";
            }
        }


        model.addAttribute("text", text);
        model.addAttribute("userName", userName);
        model.addAttribute("department", department);

        return "sign";
    }

    @RequestMapping(value = "/statistics")
    private String statistics(HttpServletRequest httpRequest, Model model) throws Exception {

        String begin = httpRequest.getParameter("begin");
        String end = httpRequest.getParameter("end");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟

        Date beginTime = sdf.parse("2017-07-24");
        Date endTime = sdf.parse(TimeUtil.today());
        try {
            if (!StringUtils.isEmpty(begin)) {
                beginTime = sdf.parse(begin);
            }
        } catch (Exception e) {
        }

        try {
            if (!StringUtils.isEmpty(end)) {
                endTime = sdf.parse(end);
            }
        } catch (Exception e) {
        }

        List<DateCount> result = orderService.getCount(beginTime, endTime);

        Map<Date, Long> signLunchMap = signService.getLunchCount(beginTime, endTime).stream()
                .collect(Collectors.toMap(DateCount::getDate, DateCount::getCount));

        Map<Date, Long> signDinnerMap = signService.getDinnerCount(beginTime, endTime).stream()
                .collect(Collectors.toMap(DateCount::getDate, DateCount::getCount));

        Map<Date, Long> signSupperMap = signService.getSupperCount(beginTime, endTime).stream()
                .collect(Collectors.toMap(DateCount::getDate, DateCount::getCount));

        for (DateCount dateCount : result) {
            Long signLunch = signLunchMap.get(dateCount.getDate());
            Long signDinner = signDinnerMap.get(dateCount.getDate());
            Long signSupper = signSupperMap.get(dateCount.getDate());

            if (dateCount.getDinnerCount() > 0) {
                dateCount.setDinnerCount(dateCount.getDinnerCount() + 3);
            }

            dateCount.setSignLunchCount(signLunch == null ? 0 : signLunch);
            dateCount.setSignDinnerCount(signDinner == null ? 0 : signDinner + 3);
            dateCount.setSignSupperCount(signSupper == null ? 0 : signSupper);
        }

        model.addAttribute("result", result);
        model.addAttribute("begin", sdf.format(beginTime));
        model.addAttribute("end", sdf.format(endTime));


        return "statistics/statistics";
    }


    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    private String detail(String date, Model model) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
        Date d = sdf.parse(date);

        Set<String> set = new HashSet();
        List<Booking> bookings = orderService.getOrdersByDate(d);
        List<Sign> lunchSign = signService.getLunchBySignTime(d);
        List<Sign> dinnerSign = signService.getDinnerBySignTime(d);
        List<Sign> supperSign = signService.getSupperBySignTime(d);

        List<String> lunchSignKey = lunchSign.stream().map(p -> p.getUserName() + "-" + p.getDepartment()).collect(Collectors.toList());
        List<String> dinnerSignKey = dinnerSign.stream().map(p -> p.getUserName() + "-" + p.getDepartment()).collect(Collectors.toList());
        List<String> supperSignKey = supperSign.stream().map(p -> p.getUserName() + "-" + p.getDepartment()).collect(Collectors.toList());


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
                detail.setSupperCount(Long.valueOf(p.getSupper()));

                detail.setSignLunchCount(lunchSignKey.contains(key) ? 1L : 0L);
                detail.setSignDinnerCount(dinnerSignKey.contains(key) ? 1L : 0);
                detail.setSignSupperCount(supperSignKey.contains(key) ? 1L : 0);

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
                detail.setSupperCount(0L);

                detail.setSignLunchCount(1L);
                detail.setSignDinnerCount(dinnerSignKey.contains(key) ? 1L : 0);
                detail.setSignSupperCount(supperSignKey.contains(key) ? 1L : 0);

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
                detail.setSupperCount(0L);

                detail.setSignLunchCount(lunchSignKey.contains(key) ? 1L : 0L);
                detail.setSignDinnerCount(1L);
                detail.setSignSupperCount(supperSignKey.contains(key) ? 1L : 0);

                set.add(key);
                result.add(detail);
            }
        });

        supperSign.stream().forEach(p -> {
            String key = p.getUserName() + "-" + p.getDepartment();
            if (!set.contains(key)) {
                DateDetailCount detail = new DateDetailCount();
                detail.setDate(d);
                detail.setUserName(p.getUserName());
                detail.setDepartment(p.getDepartment());
                detail.setLunchCount(0L);
                detail.setDinnerCount(0L);
                detail.setSupperCount(0L);

                detail.setSignLunchCount(lunchSignKey.contains(key) ? 1L : 0L);
                detail.setSignDinnerCount(dinnerSignKey.contains(key) ? 1L : 0);
                detail.setSignSupperCount(1L);

                set.add(key);
                result.add(detail);
            }
        });


        model.addAttribute("result", result);

        return "statistics/detail";
    }

    private boolean isSign(String userName, String department, int lunch, int dinner, int supper) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
        boolean signed = false;
        List<Sign> signs = signService.getByUserNameAndSignTime(userName, department, sdf.parse(TimeUtil.today()));
        for (Sign sign : signs) {
            if (sign.getLunch() == lunch && sign.getDinner() == dinner && sign.getSupper() == supper) {
                signed = true;
            }
        }
        return signed;
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
