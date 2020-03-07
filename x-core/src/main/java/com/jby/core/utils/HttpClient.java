package com.jby.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class HttpClient {

    private String url;

    private JSONObject urlParams = new JSONObject();

    HttpClient(String url) {
        this.url = url;
    }

    public static HttpClient build (String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        return new HttpClient(url);
    }

    /**
     * 添加URL参数
     * @param key
     * @param value
     * @return
     */
    public HttpClient addURLParams(String key, Object value) {
        if (value == null) {
            return this;
        }
        this.urlParams.put(key, value);
        return this;
    }

    public JSONObject post(JSONObject postJson) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        try {
            // 创建
            URI uri = this.getURI();
            HttpPost httpPost = new HttpPost(uri);
            StringEntity entity = new StringEntity(postJson.toString(), "utf-8");
            httpPost.setEntity(entity);
            httpPost.addHeader("Content-Type", "application/json");
            // 执行
            response = httpClient.execute(httpPost);

            // 返回结果转换
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String resultStr = "";
            String line;
            while ((line = in.readLine()) != null) {
                resultStr += line;
            }

            // 返回
            return (JSONObject) JSON.parse(resultStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
            httpClient.close();
        }

        return new JSONObject();
    }

    @Transactional
    public JSONObject get() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            // 创建
            URI uri = this.getURI();
            HttpGet httpGet = new HttpGet(uri);

            // 执行
            response = httpClient.execute(httpGet);

            // 返回结果转换
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String resultStr = "";
            String line;
            while ((line = in.readLine()) != null) {
                resultStr += line;
            }

            // 返回
            return (JSONObject) JSON.parse(resultStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
            httpClient.close();
        }

        return new JSONObject();
    }

    //
    private URI getURI() throws Exception {
        URIBuilder uriBuilder = new URIBuilder(this.url);
        for (String key : this.urlParams.keySet()) {
            uriBuilder.addParameter(key, this.urlParams.getOrDefault(key, "").toString());
        }
        return uriBuilder.build();
    }

}
