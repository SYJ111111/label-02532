package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.annotation.Log;
import com.blog.common.Constants;
import com.blog.common.Result;
import com.blog.dto.ArticleQueryDTO;
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
@RequestMapping("/api/admin/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        long articleCount = articleService.count();
        long publishedCount = articleService.count(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, Constants.ARTICLE_STATUS_PUBLISHED));
        long pendingCount = articleService.count(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, Constants.ARTICLE_STATUS_PENDING));
        long categoryCount = categoryService.count();
        long userCount = userService.count();
        List<Article> allArticles = articleService.list(
                new LambdaQueryWrapper<Article>().select(Article::getViewCount));
        long totalViews = allArticles.stream()
                .mapToLong(a -> a.getViewCount() != null ? a.getViewCount() : 0).sum();
        return Result.success(Map.of(
                "articleCount", articleCount,
                "publishedCount", publishedCount,
                "pendingCount", pendingCount,
                "categoryCount", categoryCount,
                "userCount", userCount,
                "totalViews", totalViews
        ));
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> list(ArticleQueryDTO query) {
        Page<Article> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .like(query.getTitle() != null && !query.getTitle().isEmpty(), Article::getTitle, query.getTitle())
                .eq(query.getCategoryId() != null, Article::getCategoryId, query.getCategoryId())
                .eq(query.getStatus() != null && !query.getStatus().isEmpty(), Article::getStatus, query.getStatus())
                .ne(query.getStatus() == null || query.getStatus().isEmpty(), Article::getStatus, Constants.ARTICLE_STATUS_DRAFT)
                .orderByDesc(Article::getCreatedAt);
        Page<Article> result = articleService.page(page, wrapper);
        List<ArticleVO> voList = convertToVOList(result.getRecords());
        return Result.success(Map.of("records", voList, "total", result.getTotal()));
    }

    @GetMapping("/{id}")
    public Result<ArticleVO> detail(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        return Result.success(convertToVO(article));
    }

    @Log("审核通过文章")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        if (!Constants.ARTICLE_STATUS_PENDING.equals(article.getStatus())) {
            throw new BusinessException("只有待审核的文章可以审核通过");
        }
        article.setStatus(Constants.ARTICLE_STATUS_PUBLISHED);
        articleService.updateById(article);
        return Result.success();
    }

    @Log("拒绝文章")
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        if (!Constants.ARTICLE_STATUS_PENDING.equals(article.getStatus())) {
            throw new BusinessException("只有待审核的文章可以拒绝");
        }
        article.setStatus(Constants.ARTICLE_STATUS_REJECTED);
        articleService.updateById(article);
        return Result.success();
    }

    @Log("下架文章")
    @PutMapping("/{id}/offline")
    public Result<Void> offline(@PathVariable Long id) {
        Article article = articleService.getById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        if (!Constants.ARTICLE_STATUS_PUBLISHED.equals(article.getStatus())) {
            throw new BusinessException("只有已发布的文章可以下架");
        }
        article.setStatus(Constants.ARTICLE_STATUS_DRAFT);
        articleService.updateById(article);
        return Result.success();
    }

    @Log("删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.removeById(id);
        return Result.success();
    }

    private List<ArticleVO> convertToVOList(List<Article> articles) {
        Map<Long, String> categoryMap = categoryService.list().stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
        Map<Long, String> userMap = userService.list().stream()
                .collect(Collectors.toMap(User::getId,
                        u -> u.getNickname() != null ? u.getNickname() : u.getUsername(), (a, b) -> a));
        return articles.stream().map(a -> {
            ArticleVO vo = new ArticleVO();
            vo.setId(a.getId());
            vo.setTitle(a.getTitle());
            vo.setSummary(a.getSummary());
            vo.setContent(a.getContent());
            vo.setCoverImage(a.getCoverImage());
            vo.setCategoryId(a.getCategoryId());
            vo.setCategoryName(a.getCategoryId() != null ? categoryMap.getOrDefault(a.getCategoryId(), "") : "");
            vo.setUserId(a.getUserId());
            vo.setAuthorName(userMap.getOrDefault(a.getUserId(), ""));
            vo.setStatus(a.getStatus());
            vo.setViewCount(a.getViewCount());
            vo.setCreatedAt(a.getCreatedAt());
            vo.setUpdatedAt(a.getUpdatedAt());
            return vo;
        }).collect(Collectors.toList());
    }

    private ArticleVO convertToVO(Article a) {
        ArticleVO vo = new ArticleVO();
        vo.setId(a.getId());
        vo.setTitle(a.getTitle());
        vo.setSummary(a.getSummary());
        vo.setContent(a.getContent());
        vo.setCoverImage(a.getCoverImage());
        vo.setCategoryId(a.getCategoryId());
        if (a.getCategoryId() != null) {
            Category c = categoryService.getById(a.getCategoryId());
            vo.setCategoryName(c != null ? c.getName() : "");
        }
        vo.setUserId(a.getUserId());
        User user = userService.getById(a.getUserId());
        vo.setAuthorName(user != null ? (user.getNickname() != null ? user.getNickname() : user.getUsername()) : "");
        vo.setStatus(a.getStatus());
        vo.setViewCount(a.getViewCount());
        vo.setCreatedAt(a.getCreatedAt());
        vo.setUpdatedAt(a.getUpdatedAt());
        return vo;
    }
}
