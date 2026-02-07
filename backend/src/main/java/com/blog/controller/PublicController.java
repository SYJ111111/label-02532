package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Constants;
import com.blog.common.Result;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.User;
import com.blog.exception.BusinessException;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import com.blog.service.UserService;
import com.blog.vo.ArticleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping("/articles")
    public Result<Map<String, Object>> articles(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, Constants.ARTICLE_STATUS_PUBLISHED)
                .eq(categoryId != null, Article::getCategoryId, categoryId)
                .orderByDesc(Article::getCreatedAt);
        Page<Article> result = articleService.page(page, wrapper);

        Map<Long, String> categoryMap = categoryService.list().stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
        Map<Long, String> userMap = userService.list().stream()
                .collect(Collectors.toMap(User::getId,
                        u -> u.getNickname() != null ? u.getNickname() : u.getUsername(), (a, b) -> a));

        List<ArticleVO> voList = result.getRecords().stream().map(a -> {
            ArticleVO vo = new ArticleVO();
            vo.setId(a.getId());
            vo.setTitle(a.getTitle());
            vo.setSummary(a.getSummary());
            vo.setCoverImage(a.getCoverImage());
            vo.setCategoryId(a.getCategoryId());
            vo.setCategoryName(a.getCategoryId() != null ? categoryMap.getOrDefault(a.getCategoryId(), "") : "");
            vo.setAuthorName(userMap.getOrDefault(a.getUserId(), ""));
            vo.setStatus(a.getStatus());
            vo.setViewCount(a.getViewCount());
            vo.setCreatedAt(a.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());

        return Result.success(Map.of("records", voList, "total", result.getTotal()));
    }

    @GetMapping("/articles/{id}")
    public Result<ArticleVO> articleDetail(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null || !Constants.ARTICLE_STATUS_PUBLISHED.equals(article.getStatus())) {
            throw new BusinessException("文章不存在");
        }
        article.setViewCount(article.getViewCount() + 1);
        articleService.updateById(article);

        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setContent(article.getContent());
        vo.setCoverImage(article.getCoverImage());
        vo.setCategoryId(article.getCategoryId());
        if (article.getCategoryId() != null) {
            Category c = categoryService.getById(article.getCategoryId());
            vo.setCategoryName(c != null ? c.getName() : "");
        }
        vo.setUserId(article.getUserId());
        User user = userService.getById(article.getUserId());
        vo.setAuthorName(user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "");
        vo.setStatus(article.getStatus());
        vo.setViewCount(article.getViewCount());
        vo.setCreatedAt(article.getCreatedAt());
        vo.setUpdatedAt(article.getUpdatedAt());
        return Result.success(vo);
    }

    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        List<Category> list = categoryService.list(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, Constants.STATUS_ENABLED)
                        .orderByAsc(Category::getSortOrder));
        return Result.success(list);
    }
}
