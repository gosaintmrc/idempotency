package com.gosaint.idempotency.config.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author gosaint
 * @Description:
 * @Date Created in 11:19 2019/1/29
 * @Modified By:
 */
@Component
public class BaseRedisComponent {

    @Autowired
    private StringRedisTemplate redisTemplate;
    public void serString(String key,Object data,Long expire_time){
        if(data instanceof String){
            String value=(String) data;
            redisTemplate.opsForValue().set(key,value);
        }
        if(expire_time!=null){
            redisTemplate.expire(key,expire_time, TimeUnit.SECONDS);
        }
    }

    public Object getString(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public void delKey(String key){
        redisTemplate.delete(key);
    }
}
