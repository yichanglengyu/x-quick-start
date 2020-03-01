package com.jby.core.repository;

import com.alibaba.fastjson.JSONObject;
import com.jby.core.PageResult;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface IBaseRepository<T, ID extends Serializable> extends JpaRepositoryImplementation<T, ID> {

    //假的删除，status=-1
    void deleteLogic(ID id) throws Exception;

    PageResult list(JSONObject json);

    List<T> getAllByProperty(Object... props);

    T getByProperty(Object... props);
}
