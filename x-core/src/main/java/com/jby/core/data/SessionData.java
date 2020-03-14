package com.jby.core.data;

import com.alibaba.fastjson.JSONObject;
import com.jby.core.utils.BeanUtils;
import lombok.Data;

@Data
public class SessionData<T> {

    // 登陆的用户实体
    private T bean;

    // 登陆用户实体类型
    private String target;

    private JSONObject otherInfo = new JSONObject();

    public SessionData(T bean) {
        this.bean = bean;
        this.target = bean.getClass().getSimpleName();
    }

    public Object getId() {
        return BeanUtils.getProperty(this.bean, "id");
    }
}
