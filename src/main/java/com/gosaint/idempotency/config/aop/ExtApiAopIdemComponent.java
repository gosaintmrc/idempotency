package com.gosaint.idempotency.config.aop;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gosaint.idempotency.annotation.ExtAPIIdempotent;
import com.gosaint.idempotency.annotation.ExtAPIToken;
import com.gosaint.idempotency.config.redis.RedisTokenComponent;
import com.gosaint.idempotency.util.ConstantUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author gosaint
 * @Description:
 * @Date Created in 11:09 2019/1/29
 * @Modified By:
 */
@Component
@Aspect
public class ExtApiAopIdemComponent {

    @Autowired
    private RedisTokenComponent redisToken;
    /**
     * 作用的类：切点
     */
    @Pointcut("execution(public * com.gosaint.idempotency.controller.*.*(..))")
    public void rlAop(){
    }

    /**
     * 前置通知转发Token参数  进行拦截的逻辑
     * @param joinPoint
     */
    @Before("rlAop()")
    public void before(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature )joinPoint.getSignature();
        //查询所有的方法上面有Token注解的
        ExtAPIToken apiToken = signature.getMethod().getDeclaredAnnotation(ExtAPIToken.class);
        if(apiToken!=null){
            /**
             * 如果存在Token注解
             * （1）从redis中获取Token,然后存储到request请求头里面
             */
            apiToken();
        }
    }

    /**
     * 环绕通知参数验证
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("rlAop()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature )proceedingJoinPoint.getSignature();
        //查询所有的方法上面有Token注解的
        ExtAPIIdempotent extAPIIdempotent =
                signature.getMethod().getDeclaredAnnotation(ExtAPIIdempotent.class);
        if(extAPIIdempotent!=null){
            //有注解的情况 有注解的说明需要进行token校验
            return extAPIIdempotent(proceedingJoinPoint, extAPIIdempotent);
        }
        //如果没有注解。直接放行执行逻辑
        Object proceed = proceedingJoinPoint.proceed();
        return proceed;
    }

    private Object extAPIIdempotent(ProceedingJoinPoint proceedingJoinPoint, ExtAPIIdempotent apiIdempotent) throws Throwable {
        HttpServletRequest request = getRequest();
        String valueType = apiIdempotent.value();
        if (StringUtils.isEmpty(valueType)) {
            response("参数错误!");
            return null;
        }
        String token=null;
        //如果存在header中 从头中获取
        if(valueType.equals(ConstantUtils.EXTAPIHEAD)){
             token = request.getHeader("token");
        }else {
            //否则从请求参数中获取
            token=request.getParameter("token");
        }
        if (StringUtils.isEmpty(token)) {
            response("参数错误!");
            return null;
        }
        boolean isToken = redisToken.checkToken(token);
        if(!isToken){
            response("请勿重复提交!");
            return null;
        }
        Object proceed = proceedingJoinPoint.proceed();
        return proceed;
    }

    private void response(final String msg) throws IOException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes.getResponse();
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        try {
            writer.println(msg);
        } catch (Exception e) {
        } finally {
            writer.close();
        }
    }

    private void apiToken() {
        getRequest().setAttribute("token",redisToken.getToken());
    }

    public HttpServletRequest getRequest(){
        ServletRequestAttributes requestAttributes = 
                (ServletRequestAttributes )RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        return request;
    }
}
