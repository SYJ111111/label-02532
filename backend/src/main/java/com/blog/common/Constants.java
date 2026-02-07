package com.blog.common;

public class Constants {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final String ARTICLE_STATUS_DRAFT = "DRAFT";
    public static final String ARTICLE_STATUS_PENDING = "PENDING";
    public static final String ARTICLE_STATUS_PUBLISHED = "PUBLISHED";
    public static final String ARTICLE_STATUS_REJECTED = "REJECTED";
    public static final Integer STATUS_ENABLED = 1;
    public static final Integer STATUS_DISABLED = 0;
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private Constants() {
    }
}
