package com.jby.core.service;



import com.alibaba.fastjson.JSONObject;
import com.jby.core.PageResult;
import com.jby.core.repository.IBaseRepository;
import com.jby.core.utils.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

@Transactional(readOnly = true)
public abstract class AlphaService<T, ID extends Serializable> {

    private IBaseRepository<T, ID> repository = null;

    /**
     * 获取service中的repository
     * @return repository
     * @throws Exception
     */
    private IBaseRepository<T, ID> repository() throws Exception {
        if (repository != null) {
            return repository;
        }

        synchronized (this) {
            if (repository == null) {

                // b.通过反射遍历子类的全部service对象，找出对应的service //获取实体类的所有属性，返回Field数组
                for (Field field : this.getClass().getDeclaredFields()) { // 遍历所有属性

                    // b.2 判断属性是object类型
                    Type type = field.getGenericType();
                    if (!(type instanceof Class)) {
                        continue;
                    }

                    // b.3 判断属性是 AlphaService的子类
                    Class<?> fieldClass = (Class<?>) type;
                    if (!IBaseRepository.class.isAssignableFrom(fieldClass)) {
                        continue;
                    }
                    if(checkT(fieldClass)) {
                        field.setAccessible(true);
                        repository = (IBaseRepository<T,ID>) field.get(this);
                        break;
                    }
                }
            }
            return  repository;
        }
    }

    /**
     * 判断对应service注入属性的泛型是否一致
     * @param fieldClazz 通过@Autowired注入的dao接口
     * @return
     */
    private boolean checkT(Class<?> fieldClazz) {
        boolean result = false;

        //1.获取本类service的泛型T
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type[] params = parameterizedType.getActualTypeArguments();

        //2.获取fieldClazz的多个接口的泛型属性
        Type[] interfaceTypes = fieldClazz.getGenericInterfaces();
        for(Type t : interfaceTypes) {
            Type[] genericType2 = ((ParameterizedType) t).getActualTypeArguments();
            for (Type t2 : genericType2) {
                if(t2.getTypeName().equals(params[0].getTypeName())) {
                    result = true;
                    break;
                }
            }
            if(result) {
                break;
            }
        }
        return result;
    }

    public T get(ID id) throws Exception {
        return repository().getOne(id);
    }

    @Transactional
    public T save(T bean) throws Exception {

        // 反射设置创建时间和更新时间
        Object id = BeanUtils.getProperty(bean, "id");
        if (id == null) {
            BeanUtils.setProperty(bean, "createTime", new Date());
        }
        BeanUtils.setProperty(bean, "updateTime", new Date());

        return repository().save(bean);
    }


    public PageResult list(JSONObject json) throws Exception{
        return repository().list(json);
    }

    public T getByProperty(Object... props) throws Exception {
        return repository().getByProperty(props);
    }

    public List<T> getAllByProperty(Object... props) throws Exception {
        return repository().getAllByProperty(props);
    }

    @Transactional
    public void delete(ID id)throws Exception {
        repository().deleteById(id);
    }

    /**
     * 逻辑删除
     * @param id
     * @throws Exception
     */
    @Transactional
    public void deleteLogic(ID id)throws Exception {
        repository().deleteLogic(id);
    }

}
