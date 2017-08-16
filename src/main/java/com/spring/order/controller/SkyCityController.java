package com.spring.order.controller;

import com.spring.order.TimeUtil;
import com.spring.order.Util;
import com.spring.order.dao.ArtistOrderRepository;
import com.spring.order.dao.UserRepository;
import com.spring.order.dto.ArtistBooking;
import com.spring.order.dto.Sign;
import com.spring.order.dto.UserDTO;
import com.spring.order.service.ArtistOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lichundong on 2017/8/11.
 */
@Controller
@RequestMapping(value = "/artist")
public class SkyCityController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtistOrderRepository artistOrderRepository;

    @Autowired
    private ArtistOrderService artistOrderService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟

            UserDTO userDTO = getUserDTO(httpRequest);

            if (userDTO == null) {
                return "artist/skycity_wode";
            }

            String tomorrowStr = TimeUtil.tomorrow();

            String orderDateStr = TimeUtil.tomorrow();

            List<ArtistBooking> artistBookings = artistOrderRepository.findByUserIdAndOrderDateGreaterThanOrderByOrderDateDesc(userDTO.getId(), sdf.parse(tomorrowStr));

            if (!CollectionUtils.isEmpty(artistBookings)) {
                ArtistBooking booking = artistBookings.get(0);

                Long orderDate = Math.max(sdf.parse(tomorrowStr).getTime(), sdf.parse(TimeUtil.tomorrow(booking.getOrderDate())).getTime());

                Date od = new Date(orderDate);

                orderDateStr = sdf.format(od);
            }

            model.addAttribute("orderDate", orderDateStr);

            return "artist/skycity";

        } catch (Exception e) {
            model.addAttribute("text", "哎呀服务器出错了");

            return "artist/success";
        }

    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String query(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟

            UserDTO userDTO = getUserDTO(httpRequest);

            if (userDTO == null) {
                return "artist/skycity_wode";
            }

            List<ArtistBooking> artistBookings = artistOrderRepository.findByUserIdAndOrderDateGreaterThanOrderByOrderDateDesc(userDTO.getId(), sdf.parse(TimeUtil.yesterday()));

            model.addAttribute("bookings", artistBookings);

            return "artist/skycity_query";

        } catch (Exception e) {
            model.addAttribute("text", "哎呀服务器出错了");

            return "artist/success";
        }
    }

    @RequestMapping(value = "/signQuery", method = RequestMethod.GET)
    public String signQuery(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟

            List<ArtistBooking> artistBookings = artistOrderRepository.findByOrderDate(sdf.parse(TimeUtil.today()));

            List<ArtistBooking> result = new ArrayList<>();

            List<ArtistBooking> sign = new ArrayList<>();

            List<ArtistBooking> signed = new ArrayList<>();


            int hour = TimeUtil.getNowOfHour();
            int lunch = 0;
            int dinner = 0;
            int supper = 0;

            String what = "";
            String text = "";


            if (hour >= 10 && hour <= 14) {
                lunch = 1;
                what = "午餐";
                result = artistBookings.stream().filter(p -> p.getLunch() == 1).collect(Collectors.toList());
                sign = result.stream().filter(p -> p.getSignLunch() == 0).collect(Collectors.toList());
                signed = result.stream().filter(p -> p.getSignLunch() == 1).collect(Collectors.toList());

            }
            if (hour >= 16 && hour <= 19) {
                dinner = 1;
                what = "晚餐";
                result = artistBookings.stream().filter(p -> p.getDinner() == 1).collect(Collectors.toList());
                sign = result.stream().filter(p -> p.getSignDinner() == 0).collect(Collectors.toList());
                signed = result.stream().filter(p -> p.getSignDinner() == 1).collect(Collectors.toList());
            }

            if (hour >= 21) {
                supper = 1;
                what = "夜宵";
                result = artistBookings.stream().filter(p -> p.getSupper() == 1).collect(Collectors.toList());
                sign = result.stream().filter(p -> p.getSignSupper() == 0).collect(Collectors.toList());
                signed = result.stream().filter(p -> p.getSignSupper() == 1).collect(Collectors.toList());
            }

            if (lunch == 0 && dinner == 0 && supper == 0) {
                text = " 不在用餐时间。午餐：10点-14点 晚餐：16点-20点 夜宵：21点-24点";
            }


            model.addAttribute("all", result);

            model.addAttribute("sign", sign);

            model.addAttribute("signed", signed);

            model.addAttribute("what", TimeUtil.today() + what);

            model.addAttribute("text", text);


            return "artist/skycity_sign_query";

        } catch (Exception e) {
            model.addAttribute("error", "哎呀服务器出错了");

            return "artist/skycity_sign_query";
        }
    }

    @RequestMapping(value = "/wode", method = RequestMethod.GET)
    public String wode(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) {
        try {
            UserDTO userDTO = getUserDTO(httpRequest);

            if (userDTO == null) {
                return "artist/skycity_wode";
            }

            model.addAttribute("user", userDTO);

            return "artist/skycity_logined";

        } catch (Exception e) {
            model.addAttribute("text", "哎呀服务器出错了");

            return "artist/success";
        }
    }


    @RequestMapping(value = "/orderDo", method = RequestMethod.POST)
    public String order(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) {
        try {
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


            UserDTO userDTO = getUserDTO(httpRequest);

            if (userDTO == null) {
                return "artist/skycity_wode";
            }

            if (TimeUtil.isTodayAfterClock(18, 00) && TimeUtil.tomorrow().equals(orderDate)) {
                model.addAttribute("text", "已经超过了下午6:00，不能再点餐了");

                return index(httpRequest, httpServletResponse, model);
            }

            ArtistBooking bookingDb = artistOrderRepository.findByUserIdAndOrderDate(userDTO.getId(), sdf.parse(orderDate));

            try {
                if (bookingDb != null) {
                    bookingDb.setName(userDTO.getName());
                    bookingDb.setPhone(userDTO.getPhone());
                    bookingDb.setSex(userDTO.getSex());
                    bookingDb.setLunch(lunch);
                    bookingDb.setDinner(dinner);
                    bookingDb.setSupper(supper);

                    artistOrderRepository.save(bookingDb);

                } else {
                    ArtistBooking booking = new ArtistBooking(userDTO.getId(), userDTO.getName(), userDTO.getPhone(), userDTO.getSex(), sdf.parse(orderDate), lunch, dinner, supper);

                    artistOrderRepository.save(booking);
                }

            } catch (Exception e) {
                if (e.getMessage().contains("uk_user_orderdate")) {
                    model.addAttribute("text", orderDate + "订餐成功");
                    return index(httpRequest, httpServletResponse, model);
                }
                throw e;
            }

            model.addAttribute("text", orderDate + "订餐成功");

            return index(httpRequest, httpServletResponse, model);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("text", "哎呀服务器出错了");
            return "artist/success";
        }
    }

    @RequestMapping(value = "/signDo", method = RequestMethod.POST)
    public String signDo(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) {
        try {
            sign(httpRequest, 1);

            httpServletResponse.sendRedirect("/artist/signQuery");

            return "artist/success";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "哎呀服务器出错了");
            return signQuery(httpRequest, httpServletResponse, model);
        }
    }

    @RequestMapping(value = "/unSignDo", method = RequestMethod.POST)
    public String unSignDo(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) {
        try {
            sign(httpRequest, 0);

            httpServletResponse.sendRedirect("/artist/signQuery");

            return "artist/success";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "哎呀服务器出错了");
            return signQuery(httpRequest, httpServletResponse, model);
        }
    }

    private void sign(HttpServletRequest httpRequest, int sign) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟

        String userId = httpRequest.getParameter("userId");

        int hour = TimeUtil.getNowOfHour();

        if (hour >= 10 && hour <= 14) {
            artistOrderService.signLunch(Integer.valueOf(userId), sdf.parse(TimeUtil.today()), sign);
        }

        if (hour >= 16 && hour <= 19) {
            artistOrderService.signDinner(Integer.valueOf(userId), sdf.parse(TimeUtil.today()), sign);
        }

        if (hour >= 21) {
            artistOrderService.signSupper(Integer.valueOf(userId), sdf.parse(TimeUtil.today()), sign);
        }

    }

    private UserDTO getUserDTO(HttpServletRequest httpRequest) {
        int userId = getUserId(httpRequest);

        if (userId == 0) {
            return null;
        }

        UserDTO userDTO = userRepository.findById(userId);

        return userDTO;
    }


    private int getUserId(HttpServletRequest httpRequest) {
        try {
            String userStr = Util.getParamByCookie(httpRequest, Util.cookieName_userID);
            if (StringUtils.isEmpty(userStr)) {
                return 0;
            }
            String[] user = userStr.split("_");

            if (user.length != 2) {
                return 0;
            }

            Integer userId = Integer.valueOf(user[0]);

            return userId == null ? 0 : userId.intValue();

        } catch (Exception e) {
            return 0;
        }

    }
}
