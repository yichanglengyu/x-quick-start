package com.jby.core.repository;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jby.core.PageResult;
import com.jby.core.data.SortBuilder;
import com.jby.core.utils.BeanUtils;
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
        System.out.println("查询json = " + json.toJSONString());
        Sort sort = SortBuilder.create(json.getJSONObject("sort")).getSort();

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
        List<T> list = this.getAllByProperty(props);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<T> getAllByProperty(Object[] props) {
        JSONObject json = new JSONObject();
        for(int i = 0; i < props.length / 2; i++){
            String propName = props[i * 2].toString();
            Object propValue = props[i * 2 + 1];
            json.put(propName, propValue);
        }

        Sort sort = Sort.unsorted();
        if (props.length % 2 > 0) { // 有排序字段
            sort = (Sort) props[props.length -1];
        }

        Specification<T> specification = createSpecification(json);
        return findAll(specification, sort);
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
                if (value instanceof String && StringUtils.isEmpty(value)) {
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
        // jpa查询中， pageNo = 0 为首页，为了方便理解，使用时1为首页，所以这里做特殊处理
        int pageNo = (int) json.getOrDefault("pageNo", 0) - 1;
        int pageSize = (int) json.getOrDefault("pageSize", 15);
        if (pageNo == -1) {
            return null;
        }
        return sort == null ? PageRequest.of(pageNo, pageSize) : PageRequest.of(pageNo, pageSize, sort);
    }

    @Override
    public void deleteLogic(ID id) throws Exception {
        T bean = getOne(id);
        if(bean != null) {

            BeanUtils.setProperty(bean, "status", -1);
            save(bean);
        }
    }


}
