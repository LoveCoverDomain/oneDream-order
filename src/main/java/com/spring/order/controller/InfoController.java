package com.spring.order.controller;

import com.spring.order.*;
import com.spring.order.dao.UserRepository;
import com.spring.order.dto.Booking;
import com.spring.order.dto.Sign;
import com.spring.order.dto.UserDTO;
import com.spring.order.service.OrderService;
import com.spring.order.service.SignService;
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
    private UserRepository userRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SignService signService;
    @Autowired
    private SkyCityController skyCityController;


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

    @RequestMapping(value = "/artist/register", method = RequestMethod.POST)
    public String register(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) {
        try {
            String name = httpRequest.getParameter("name");
            String phone = httpRequest.getParameter("phone");
            String email = httpRequest.getParameter("email");
            String password = httpRequest.getParameter("password");
            String sex = httpRequest.getParameter("sex");
            String desc = httpRequest.getParameter("description");

            if (StringUtils.isEmpty(name) || StringUtils.isEmpty(phone)) {
                model.addAttribute("text", "姓名、电话不能为空");
                return "artist/success";
            }

            if (StringUtils.isEmpty(password)) {
                model.addAttribute("text", "必须设置密码");
                return "artist/success";
            }

            UserDTO userDTO = new UserDTO();
            userDTO.setName(name);
            userDTO.setPassWord(password);
            userDTO.setPhone(Long.valueOf(phone));
            userDTO.setSex(sex);
            userDTO.setEmail(email);
            userDTO.setDescription(desc != null ? desc.trim() : desc);

            userDTO = userRepository.save(userDTO);

            addCookie(httpServletResponse, Util.cookieName_userID, String.valueOf(userDTO.getId()) + "_" + phone);

            model.addAttribute("text", "注册成功");

            return skyCityController.wode(httpRequest,httpServletResponse,model);

        } catch (Exception e) {
            if (e.getMessage().contains("uk_phone")) {
                model.addAttribute("text", "手机已经被注册");
                return "artist/success";
            }
            model.addAttribute("text", "哎呀服务器出错了");
            return "artist/success";
        }
    }

    @RequestMapping(value = "/artist/update", method = RequestMethod.POST)
    public String artistUpdate(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) {
        try {
            String name = httpRequest.getParameter("name");
            String phone = httpRequest.getParameter("phone");
            String email = httpRequest.getParameter("email");
            String id = httpRequest.getParameter("id");
            String sex = httpRequest.getParameter("sex");
            String desc = httpRequest.getParameter("description");

            if (StringUtils.isEmpty(name) || StringUtils.isEmpty(phone)) {
                model.addAttribute("text", "姓名、电话不能为空");
                return skyCityController.wode(httpRequest, httpServletResponse, model);
            }

            UserDTO userDTO = new UserDTO();
            userDTO.setId(Integer.valueOf(id));
            userDTO.setName(name);
            userDTO.setPhone(Long.valueOf(phone));
            userDTO.setSex(sex);
            userDTO.setEmail(email);
            userDTO.setDescription(desc != null ? desc.trim() : desc);

            userRepository.save(userDTO);

            model.addAttribute("text", "个人信息修改成功");

            return skyCityController.wode(httpRequest, httpServletResponse, model);

        } catch (Exception e) {
            model.addAttribute("text", "哎呀服务器出错了");
            return "artist/success";
        }

    }

    @RequestMapping(value = "/artist/updatePassword", method = RequestMethod.POST)
    public String updatePassword(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) {
        try {
            String id = httpRequest.getParameter("id");
            String oldPassword = httpRequest.getParameter("oldPassword");
            String newPassword = httpRequest.getParameter("newPassword");
            String newPassword1 = httpRequest.getParameter("newPassword1");


            if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(newPassword1)) {
                model.addAttribute("text", "旧密码，新密码，确认密码不能为空");
                return skyCityController.wode(httpRequest, httpServletResponse, model);

            }

            if (!newPassword.equals(newPassword1)) {
                model.addAttribute("text", "新密码，确认密码不一致");
                return skyCityController.wode(httpRequest, httpServletResponse, model);
            }

            UserDTO userDTO = userRepository.findById(Integer.valueOf(id));

            if (!userDTO.getPassWord().equals(oldPassword)) {
                model.addAttribute("text", "旧密码输入不正确");
                return skyCityController.wode(httpRequest, httpServletResponse, model);
            }

            userDTO.setPassWord(newPassword);

            userRepository.save(userDTO);

            model.addAttribute("text", "密码修改成功");

            return skyCityController.wode(httpRequest, httpServletResponse, model);

        } catch (Exception e) {
            model.addAttribute("text", "哎呀服务器出错了");
            return "artist/success";
        }
    }

    @RequestMapping(value = "/artist/login", method = RequestMethod.POST)
    public String artistLogin(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) {
        try {
            String phone = httpRequest.getParameter("phone");
            String password = httpRequest.getParameter("password");


            if (StringUtils.isEmpty(phone)) {
                model.addAttribute("text", "电话不能为空");
                return "artist/skycity_wode";
            }

            UserDTO userDTO = userRepository.findByPhone(Long.valueOf(phone));

            if (userDTO != null && userDTO.getPassWord().equals(password)) {
                addCookie(httpServletResponse, Util.cookieName_userID, String.valueOf(userDTO.getId()) + "_" + phone);

                httpServletResponse.sendRedirect("/artist/index");
            }

            model.addAttribute("text", "手机或密码错误");

            return "artist/skycity_wode";

        } catch (Exception e) {

            model.addAttribute("text", "哎呀服务器出错了");
            return "artist/skycity_wode";
        }
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
