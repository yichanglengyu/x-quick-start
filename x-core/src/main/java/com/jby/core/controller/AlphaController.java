package com.jby.core.controller;

import com.jby.core.cache.SessionCache;
import com.jby.core.data.Result;
import com.jby.core.data.SessionData;
import com.jby.core.exception.BusinessLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
public class AlphaController extends BaseController{


    /**
     * 获取用户登陆信息
     * @return
     */
    public SessionData getSession() {
        String token = request.getHeader("X-Token");
        SessionData sessionData = sessionCache.getSession(token);

        if (sessionData == null) {
            breaks(401, "请先登陆");
        }

        // 校验token过期



        return sessionData;
    }

    @ExceptionHandler(value = { BusinessLogicException.class, Exception.class})
    public Result handleException(Exception e) {
        if (e instanceof BusinessLogicException) {
            log.info("handler.BusinessLogicException:" + e.getMessage());
            return Result.build().ERROR(((BusinessLogicException) e).getCode(), e.getMessage());
        }

        log.info("handler.Exception:" + e.getMessage());
        e.printStackTrace();
        return Result.build().ERROR(0, e.getMessage());
    }

    @Autowired
    public HttpServletRequest request;

    @Autowired
    private SessionCache sessionCache;
}
