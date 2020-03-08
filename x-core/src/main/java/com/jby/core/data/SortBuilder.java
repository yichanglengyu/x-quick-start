package com.jby.core.data;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Data
public class SortBuilder {

    private JSONObject sortJson = new JSONObject();

    public static SortBuilder create(String field, String direction) {
        SortBuilder builder = new SortBuilder();
        builder.getSortJson().put(field, direction);
        return builder;
    }

    public static SortBuilder create() {
        return new SortBuilder();
    }

    public static SortBuilder create(JSONObject json) {
        SortBuilder builder = new SortBuilder();
        builder.setSortJson(json);
        return builder;
    }

    public SortBuilder add(String field, String direction) {
        this.sortJson.put(field, direction);
        return this;
    }

    public SortBuilder asc(String field) {
        this.sortJson.put(field, "asc");
        return this;
    }

    public SortBuilder desc(String field) {
        this.sortJson.put(field, "desc");
        return this;
    }
    /**
     * {"id": "desc", "seq": "asc"}
     * 根据id降序, seq升序
     * @return
     */
    public Sort getSort() {
        if (this.sortJson == null || this.sortJson.isEmpty()) {
            return Sort.unsorted();
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (String key : this.sortJson.keySet()) {
            orders.add(new Sort.Order(Sort.Direction.fromString((String)this.sortJson.getOrDefault(key, "asc")), key));
        }
        Sort sort = Sort.by(orders);
        return sort;
    }
}
