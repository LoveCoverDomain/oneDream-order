package com.spring.order.controller;

import com.spring.order.TimeUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;

/**
 * Created by lichundong on 2017/8/11.
 */
@Controller
@RequestMapping(value = "/artist")
public class SkyCityController {
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {

        String tomorrowStr = TimeUtil.tomorrow();

        String orderDateStr = TimeUtil.tomorrow();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟


        model.addAttribute("orderDate", orderDateStr);


        model.addAttribute("tomorrow", tomorrowStr);

        return "artist/skycity";
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public String query(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {

        return "artist/skycity_query";
    }

    @RequestMapping(value = "/wode", method = RequestMethod.GET)
    public String wode(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {

        return "artist/skycity_wode";
    }
}
