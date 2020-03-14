package com.jby.core.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jby.core.data.SessionData;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class SessionCache {

    /**
     * 登陆信息缓存
     * key为登陆token，value为sessionData
     */
    private Cache<String, SessionData> sessionCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.HOURS)
            .build();


    /**
     * 获取登陆信息
     * @param token
     * @return
     */
    public SessionData getSession(String token) {
        return sessionCache.getIfPresent(token);
    }

    /**
     * 设置登陆信息
     * @param token
     * @param sessionData
     * @return
     */
    public void setSession(String token, SessionData sessionData) {
        sessionCache.put(token, sessionData);
    }

    /**
     * 使某个缓存失效
     * @param token
     */
    public void invalid(String token) {
        sessionCache.invalidate(token);
    }

}
