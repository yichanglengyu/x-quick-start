package com.jby.core;

import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
public class PageResult {

    // 分页的条件
    private Pageable pageable;

    // 总页数
    private Integer totalPages;

    // 查询数据总数
    private Integer totalElements;

    private List content;


    public PageResult(Pageable pageable, Integer totalPages, Integer totalElements, List content) {
        this.pageable = pageable;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.content = content;
    }

}
