package com.jby.core.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class Result {

    private JSONObject data = new JSONObject();

    private Integer code;

    private String message;

    private Result(JSONObject jsonObject, int code, String message) {
        this.data = jsonObject;
        this.code = code;
        this.message = message;
    }

    public static Result build(Object bean) {
        Object o = JSON.toJSON(bean);
        return new Result((JSONObject) o, 0, "");
    }

    public static Result build() {
        return new Result(new JSONObject(), 0, "");
    }

    public Result element(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public Result SUCCESS(JSONObject data) {
        this.data = data;
        this.code = 20000;
        this.message = "操作成功";
        return this;
    }

    public Result SUCCESS() {
        this.code = 20000;
        this.message = "操作成功";
        return this;
    }

    public Result FAIL() {
        this.code = 0;
        this.message = "操作失败";
        return this;
    }

    public Result ERROR(Integer code, String message) {
        this.code = code;
        this.message = message;
        return this;
    }
}
