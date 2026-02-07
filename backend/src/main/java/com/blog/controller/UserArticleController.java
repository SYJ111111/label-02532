package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.annotation.Log;
import com.blog.common.Constants;
import com.blog.common.Result;
import com.blog.dto.ArticleDTO;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.exception.BusinessException;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import com.blog.vo.ArticleVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/article")
@RequiredArgsConstructor
public class UserArticleController {

    private final ArticleService articleService;
    private final CategoryService categoryService;

    @GetMapping("/my")
    public Result<Map<String, Object>> myArticles(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getUserId, userId)
                .eq(status != null && !status.isEmpty(), Article::getStatus, status)
                .orderByDesc(Article::getCreatedAt);
        Page<Article> result = articleService.page(page, wrapper);
        List<ArticleVO> voList = convertToVOList(result.getRecords());
        return Result.success(Map.of("records", voList, "total", result.getTotal()));
    }

    @GetMapping("/{id}")
    public Result<ArticleVO> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Article article = articleService.getById(id);
        if (article == null || !article.getUserId().equals(userId)) {
            throw new BusinessException("文章不存在");
        }
        return Result.success(convertToVO(article));
    }

    @Log("用户保存文章")
    @PostMapping("/save")
    public Result<Long> save(@Valid @RequestBody ArticleDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Article article;
        if (dto.getId() != null) {
            article = articleService.getById(dto.getId());
            if (article == null || !article.getUserId().equals(userId)) {
                throw new BusinessException("文章不存在");
            }
        } else {
            article = new Article();
            article.setViewCount(0);
            article.setUserId(userId);
        }
        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setContent(dto.getContent());
        article.setCoverImage(dto.getCoverImage());
        article.setCategoryId(dto.getCategoryId());
        // 编辑后重置为草稿状态，需要重新提交审核
        article.setStatus(Constants.ARTICLE_STATUS_DRAFT);
        articleService.saveOrUpdate(article);
        return Result.success(article.getId());
    }

    @Log("用户提交文章审核")
    @PostMapping("/{id}/submit")
    public Result<Void> submitForReview(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Article article = articleService.getById(id);
        if (article == null || !article.getUserId().equals(userId)) {
            throw new BusinessException("文章不存在");
        }
        if (!Constants.ARTICLE_STATUS_DRAFT.equals(article.getStatus()) 
                && !Constants.ARTICLE_STATUS_REJECTED.equals(article.getStatus())) {
            throw new BusinessException("只有草稿或被拒绝的文章可以提交审核");
        }
        article.setStatus(Constants.ARTICLE_STATUS_PENDING);
        articleService.updateById(article);
        return Result.success();
    }

    @Log("用户删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Article article = articleService.getById(id);
        if (article == null || !article.getUserId().equals(userId)) {
            throw new BusinessException("文章不存在");
        }
        articleService.removeById(id);
        return Result.success();
    }

    private List<ArticleVO> convertToVOList(List<Article> articles) {
        Map<Long, String> categoryMap = categoryService.list().stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
        return articles.stream().map(a -> {
            ArticleVO vo = new ArticleVO();
            vo.setId(a.getId());
            vo.setTitle(a.getTitle());
            vo.setSummary(a.getSummary());
            vo.setCategoryId(a.getCategoryId());
            vo.setCategoryName(a.getCategoryId() != null ? categoryMap.getOrDefault(a.getCategoryId(), "") : "");
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
        vo.setStatus(a.getStatus());
        vo.setViewCount(a.getViewCount());
        vo.setCreatedAt(a.getCreatedAt());
        vo.setUpdatedAt(a.getUpdatedAt());
        return vo;
    }
}
