package com.spring.order.controller;

import com.spring.order.dto.Suggest;
import com.spring.order.dao.SuggestRepository;
import com.spring.order.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lichundong on 2017/7/26.
 */
@Controller
public class SuggestController {

    @Autowired
    private SuggestRepository suggestRepository;

    @RequestMapping(value = "/suggestDo", method = RequestMethod.POST)
    public String suggest(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse, Model model) throws Exception {
        String object = httpRequest.getParameter("object");
        String content = httpRequest.getParameter("content");

        if (StringUtils.isEmpty(object) || StringUtils.isEmpty(content)) {
            model.addAttribute("text", "内容不能为空");
            model.addAttribute("button", "返回");
            return "success";
        }

        String userName = Util.getParamByCookie(httpRequest, Util.cookieName_userName);
        String department = Util.getParamByCookie(httpRequest, Util.cookieName_department);

        Suggest suggest = new Suggest(userName, department, object, content);

        suggestRepository.save(suggest);


        model.addAttribute("text", "提交成功");
        model.addAttribute("button", "返回");
        return "success";

    }
}
