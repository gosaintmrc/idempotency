package com.gosaint.idempotency.controller;

import javax.servlet.http.HttpServletRequest;

import com.gosaint.idempotency.annotation.ExtAPIIdempotent;
import com.gosaint.idempotency.annotation.ExtAPIToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author gosaint
 * @Description:
 * @Date Created in 11:03 2019/1/29
 * @Modified By:
 */
@Controller
public class IdempotencyController {

    private static final Logger logger =  LoggerFactory.getLogger(IdempotencyController.class);

    /**
     * 页面测试
     * @param model
     * @return
     * 统一设置Token
     */
    @RequestMapping("/idemo")
    @ExtAPIToken
    public ModelAndView ideoFormvertify(Model model){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("order");
        return modelAndView;
    }

    @RequestMapping(value = "/idemo/trans")
    @ResponseBody
    @ExtAPIIdempotent(value = "form")
    public String addUserPage(HttpServletRequest request) {
        return "添加成功！";
    }

}
