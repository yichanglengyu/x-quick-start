package com.jby.core.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    protected JSONObject getParamJSONObject(HttpServletRequest request, String key) throws Exception{
        String jsonStr = request.getParameter(key);
        JSONObject result = (JSONObject)JSON.parse(jsonStr);
        return result == null ? new JSONObject() : result;
    }

    protected JSONArray getParamJSONArray(HttpServletRequest request, String key) throws Exception{
        String jsonStr = request.getParameter(key);
        return (JSONArray)JSON.parse(jsonStr);
    }

}
