package com.gosaint.idempotency.controller;

import javax.servlet.http.HttpServletRequest;

import com.gosaint.idempotency.config.redis.RedisTokenComponent;
import com.gosaint.idempotency.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gosaint
 * @Description:
 * @Date Created in 14:43 2019/1/29
 * @Modified By:
 */
@RestController
public class TestController {

    @Autowired
    private RedisTokenComponent redisTokenComponent;

    /**
     * 测试Token的生成
     * @return
     */
    @RequestMapping(value = "getToken")
    public String getToken() {
        return redisTokenComponent.getToken();
    }

    @RequestMapping(value = "/idemo/trans", produces = "application/json;charset=utf-8")
    public String addUser(@RequestBody User user, HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            return "请求参数错误！";
        }
        boolean tokenOk=redisTokenComponent.checkToken(token);
        if (!tokenOk) {
            return "请勿重复提交!";
        }
        //执行正常的业务逻辑
        System.out.println("user info:" + user);
        return "添加成功!";
    }

}
