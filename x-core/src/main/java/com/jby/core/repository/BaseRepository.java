package com.jby.core.repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jby.core.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BaseRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements IBaseRepository<T, ID>{


    private final Class<T> clazz;

    protected final EntityManager entityManager;

    public BaseRepository(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
        this.clazz = domainClass;
    }

    @Override
    public PageResult list(JSONObject json) {
        Sort sort = createSort(json.getJSONObject("sort"));
        Pageable pageable = createPageable(json, sort);

        JSONObject filter = (JSONObject)json.getOrDefault("filter", new JSONObject());
        Specification<T> specification = createSpecification(filter);

        PageResult pageResult;
        if (pageable == null){
            // 不分页查询
            List list = findAll(specification, sort);
            pageResult = new PageResult(null, 1, list.size(), list);
        } else {
            Page page = findAll(specification, pageable);
            pageResult = new PageResult(page.getPageable(), page.getTotalPages(), (int) page.getTotalElements(), page.getContent());
        }
        return pageResult;
    }


    @Override
    public T getByProperty(Object[] props) {
        JSONObject json = new JSONObject();
        for(int i = 0; i < props.length / 2; i++){
            String propName = props[i * 2].toString();
            Object propValue = props[i * 2 + 1];
            json.put(propName, propValue);
        }
        Specification<T> specification = createSpecification(json);
        return findOne(specification).orElse(null);
    }

    @Override
    public List<T> getAllByProperty(Object[] props) {
        JSONObject json = new JSONObject();
        for(int i = 0; i < props.length / 2; i++){
            String propName = props[i * 2].toString();
            Object propValue = props[i * 2 + 1];
            json.put(propName, propValue);
        }

        Specification<T> specification = createSpecification(json);
        return findAll(specification);
    }

    private Specification<T> createSpecification(JSONObject filter) {
        return (Specification<T>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicatesList = new ArrayList<>();
            for (String key : filter.keySet()) {
                String[] strs = key.split("\\$");
                String filed = strs[0];
                String expression = strs.length > 1 ? strs[1] : "eq";

                // 如果值为字符串，且值为空串，则条件无效
                Object value = filter.get(key);
                if (value instanceof String && !StringUtils.isEmpty(value)) {
                    continue;
                }

                switch (expression) {
                    case "like" : {
                        predicatesList.add(criteriaBuilder.like(root.get(filed), '%' + (String)value + '%'));
                        break;
                    }
                    case "eq" : predicatesList.add(criteriaBuilder.equal(root.get(filed), value));
                        break;

                    case "in" : {
                        CriteriaBuilder.In in = criteriaBuilder.in(root.get(filed));
                        JSONArray array = filter.getJSONArray(key);
                        for (int i = 0; i < array.size(); i++) {
                            in.value(array.get(i));
                        }
                        predicatesList.add(in);
                        break;
                    }
                    // 默认eq查询eq
                    default : predicatesList.add(criteriaBuilder.equal(root.get(filed), value));

                }

            }
            return criteriaQuery.where(predicatesList.toArray(new Predicate[predicatesList.size()])).getRestriction();
        };
    }

    private Pageable createPageable(JSONObject json, Sort sort) {
        int pageNo = (int) json.getOrDefault("pageNo", 0);
        int pageSize = (int) json.getOrDefault("pageSize", 15);
        if (pageNo == 0 && pageSize == 0) {
            return null;
        }
        return sort == null ? PageRequest.of(pageNo, pageSize) : PageRequest.of(pageNo, pageSize, sort);
    }

    private Sort createSort(JSONObject json) {
        if (json == null || json.isEmpty()) {
            return Sort.unsorted();
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (String key : json.keySet()) {
            orders.add(new Sort.Order(Sort.Direction.fromString((String)json.getOrDefault(key, "asc")), key));
        }
        Sort sort = Sort.by(orders);
        return sort;
    }


    /**
     * !!!!!!!!!!!!未完成
     * 反射设置bean的属性值
     * @param bean
     * @param name
     * @param value
     * @return
     * @throws Exception
     */
    private boolean setBeanProperty(Object bean, String name, Object value) throws Exception{

        System.out.println(bean.getClass().getSimpleName() + "." + name + ":");
        for(Field field : bean.getClass().getDeclaredFields()){
            String fieldName = field.getName();
            System.out.println(fieldName + ",");
            if(fieldName.equals(name)){
                field.setAccessible(true);
                field.set(bean, value);
                return true;//表示匹配到了
            }
        }
        return false;//表示没有匹配到这个字段


       /* Field field = bean.getClass().getDeclaredField(fieldName.hashCode());
        if (field != null) {
            field.setAccessible(true);
            field.set(bean, value);
            return true;//表示匹配到了
        }
        return false;*/
    }

    @Override
    public void deleteLogic(ID id) throws Exception {
        T bean = getOne(id);
        if(bean != null) {

            setBeanProperty(bean, "status", -1);
            save(bean);
        }
    }


}
