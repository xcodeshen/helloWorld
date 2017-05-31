package com.liebao.go7881.trade.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liebao.go7881.trade.api.Constents;
import com.liebao.go7881.trade.api.common.excpetion.ApiExcpetion;
import com.liebao.go7881.trade.api.common.redis.JsonRedisTemplate;
import com.liebao.go7881.trade.api.entity.LoginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.TimeUnit;

/**
 * Created by chenliang
 * Date  2016/12/8.
 */
public class BaseApi {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    protected JsonRedisTemplate redisTemplate;
    @ExceptionHandler
    public ModelAndView handException(Exception exception){
        ModelAndView view  = new ModelAndView("api/base");
        if(exception instanceof  ApiExcpetion){
            ApiExcpetion exc = (ApiExcpetion)exception;
            view.addObject("retCode", exc.getCode());
            view.addObject("retMsg", exc.getMsg());
        }else{
            view.addObject("retCode", Constents.RetCode.error);
            view.addObject("retMsg", "系统错误");
            logger.error("系统错误",exception);
        }
        return view;
    }

    public static void main(String[] args){
    	System.out.println("add by shenjun");
    }
    protected LoginInfo validationOperatorKey(String operatorKey){
        if(redisTemplate.hasKey(Constents.REDIS_LOGIN_MAP+operatorKey)){
            LoginInfo info = (LoginInfo) redisTemplate.boundValueOps(Constents.REDIS_LOGIN_MAP+operatorKey).get();
            redisTemplate.boundValueOps(Constents.REDIS_LOGIN_MAP+info.getOperatorKey()).expire(24, TimeUnit.HOURS);
            return info;
        }else{
            logger.error("validation operator key  error:{}",operatorKey);
            throw new ApiExcpetion(Constents.RetCode.operatorKeyFail,"您的账号已在其他终端登录，请重新登录");
        }
    }

    protected HttpEntity getJsonParam(Object obj) throws Exception{
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        ObjectMapper mapper = new ObjectMapper();
        HttpEntity<String> formEntity = new HttpEntity<String>(mapper.writeValueAsString(obj), headers);
        return formEntity;
    }

}
