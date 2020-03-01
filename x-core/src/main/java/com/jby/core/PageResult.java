package com.jby.core;

import org.springframework.data.domain.Pageable;

import java.util.List;

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

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    public List getContent() {
        return content;
    }

    public void setContent(List content) {
        this.content = content;
    }
}
