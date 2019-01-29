package com.gosaint.idempotency.config.redis;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author gosaint
 * @Description:
 * @Date Created in 11:28 2019/1/29
 * @Modified By:
 */
@Component
public class RedisTokenComponent {


    @Autowired
    private BaseRedisComponent redisComponent;
    private long TOKENTIME=60*60;

    /**
     * 将Token存入Redis中,设置过期时间为60min
     * @return
     */
    public String getToken() {
        String token = "token"+ UUID.randomUUID();
        redisComponent.serString(token,token,TOKENTIME);
        return token;
    }

    /**
     * 检查Token
     * @param tokenKey
     * @return
     */
    public boolean checkToken(String tokenKey){
        String tokenValue=(String)redisComponent.getString(tokenKey);
        if(StringUtils.isEmpty(tokenValue)){
            return false;
        }
        // 保证每个接口对应的token只能访问一次，保证接口幂等性问题
        redisComponent.delKey(tokenKey);
        return true;
    }
}
