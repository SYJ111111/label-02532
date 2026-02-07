package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.annotation.Log;
import com.blog.common.Result;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.exception.BusinessException;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ArticleService articleService;

    @GetMapping("/list")
    public Result<List<Category>> list() {
        List<Category> list = categoryService.list(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder)
        );
        return Result.success(list);
    }

    @Log("保存分类")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new BusinessException("分类名称不能为空");
        }
        categoryService.saveOrUpdate(category);
        return Result.success();
    }

    @Log("删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        long count = articleService.count(
                new LambdaQueryWrapper<Article>().eq(Article::getCategoryId, id)
        );
        if (count > 0) {
            throw new BusinessException("该分类下存在文章，无法删除");
        }
        categoryService.removeById(id);
        return Result.success();
    }
}
