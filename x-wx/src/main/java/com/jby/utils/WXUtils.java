package com.jby.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jby.config.WXConfig;
import com.jby.core.data.Pair;
import com.jby.core.utils.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 微信工具类
 */
@Component
public class WXUtils {

    // 获取微信用户openid URL
    private static String WX_OPEN_ID_URL = "https://api.weixin.qq.com/sns/jscode2session";


    // 获取微信accessToken URL
    private static String WX_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";


    // 微信公众平台获取用户信息 URL
    private static String WX_USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info";


    /**
     * 微信access_token缓存
     */
    private Pair<String, LocalDateTime> accessTokenCache = Pair.of(null, LocalDateTime.now());

    /**
     *
     * 登录凭证校验。通过 wx.login() 接口获得临时登录凭证 code 后传到开发者服务器调用此接口完成登录流程
     * @param code 小程序端获取的code
     * @return
     */
    public JSONObject code2Session(String code) throws Exception{
        JSONObject result = HttpClient.build(WX_OPEN_ID_URL)
                .addURLParams("appid", wxConfig.getAppId())
                .addURLParams("secret", wxConfig.getSecret())
                .addURLParams("js_code", code)
                .addURLParams("grant_type", "authorization_code")
                .get();
        return result;
    }

    /**
     * 获取小程序用户的openid
     * @param code
     * @return
     * @throws Exception
     */
    public String getWxOpenId(String code) throws Exception {
        JSONObject result = this.code2Session(code);
        String wxOpenid = (String) result.getOrDefault("openid", "");
        if (StringUtils.isEmpty(wxOpenid)) { // 错误
            throw new Exception(result.getString("errmsg"));
        }
        return wxOpenid;
    }


    /**
     * 获取小程序全局唯一后台接口调用凭据（access_token）。调调用绝大多数后台接口时都需使用 access_token，开发者需要进行妥善保存。
     * @return
     * @throws Exception
     */
    public String getAccessToken() throws Exception {
        // 校验缓存access_token是否过期
        if (this.accessTokenCache.getSecond().isAfter(LocalDateTime.now())) {
            return this.accessTokenCache.getFirst();
        }

        JSONObject result = HttpClient.build(WX_ACCESS_TOKEN_URL)
                .addURLParams("appid", wxConfig.getAppId())
                .addURLParams("secret", wxConfig.getSecret())
                .addURLParams("grant_type", "client_credential")
                .get();

        String accessToken = result.getString("access_token");
        this.accessTokenCache.setFirst(accessToken);
        this.accessTokenCache.setSecond(LocalDateTime.now().plusSeconds(result.getInteger("expires_in") - 200));

        return accessToken;
    }

    /**
     * 获取微信用户信息
     * @param wxOpenid   用户的openid
     * @return
     * @throws Exception
     */
    /*public JSONObject getWXUserInfo(String wxOpenid) throws Exception {
        JSONObject result = HttpClient.build(WX_USER_INFO_URL)
                .addURLParams("access_token", this.getAccessToken())
                .addURLParams("openid", wxOpenid)
                .addURLParams("lang", "zh_CN")
                .get();

        return result;
    }*/

    @Autowired
    private WXConfig wxConfig;
}
