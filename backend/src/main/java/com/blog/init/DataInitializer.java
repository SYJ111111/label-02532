package com.blog.init;

import com.blog.common.Constants;
import com.blog.entity.Category;
import com.blog.entity.User;
import com.blog.service.CategoryService;
import com.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final CategoryService categoryService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initUsers();
        initCategories();
    }

    private void initUsers() {
        if (userService.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("管理员");
            admin.setRole(Constants.ROLE_ADMIN);
            admin.setStatus(Constants.STATUS_ENABLED);
            userService.save(admin);

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setNickname("普通用户");
            user.setRole(Constants.ROLE_USER);
            user.setStatus(Constants.STATUS_ENABLED);
            userService.save(user);

            log.info("初始化默认用户完成");
        }
    }

    private void initCategories() {
        if (categoryService.count() == 0) {
            String[] names = {"技术分享", "生活随笔", "读书笔记", "项目实战"};
            String[] descs = {"技术相关文章", "生活感悟与随笔", "读书心得与笔记", "项目经验与实战"};
            for (int i = 0; i < names.length; i++) {
                Category category = new Category();
                category.setName(names[i]);
                category.setDescription(descs[i]);
                category.setSortOrder(i + 1);
                category.setStatus(Constants.STATUS_ENABLED);
                categoryService.save(category);
            }
            log.info("初始化默认分类完成");
        }
    }
}
