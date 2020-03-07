package com.jby.core.utils;

import java.lang.reflect.Field;

public class BeanUtils {

    public static <T> T setProperty(T bean, String key, Object value){
        try {
            Field field = bean.getClass().getDeclaredField(key);
            field.setAccessible(true);
            field.set(bean, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return bean;
        }
        return bean;
    }

    public static <T> Object getProperty(T bean, String key) {
        try {
            Field field = bean.getClass().getDeclaredField(key);
            field.setAccessible(true);
            return field.get(bean);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
