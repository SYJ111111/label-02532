package com.blog.dto;

import lombok.Data;

@Data
public class ArticleQueryDTO {
    private String title;
    private Long categoryId;
    private String status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
