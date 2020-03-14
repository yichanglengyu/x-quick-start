package com.jby.core.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jby.core.exception.BusinessLogicException;

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


    protected static void breaks(String message) throws BusinessLogicException {
        throw new BusinessLogicException(0, message);
    }

    protected static void breaks(Integer code, String message ) throws BusinessLogicException {
        throw new BusinessLogicException(code, message);
    }


}
