package com.fu.news.config;

import com.fu.news.entity.Category;
import com.fu.news.entity.News;
import com.fu.news.entity.User;
import com.fu.news.repository.CategoryRepository;
import com.fu.news.repository.NewsRepository;
import com.fu.news.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Component nạp dữ liệu khởi tạo (Data Seeder) cho FUNewsManagementSystem.
 * Đảm bảo tính Idempotent: Kiểm tra sự tồn tại trước khi tạo mới để chạy lại
 * nhiều lần không sinh bản ghi trùng hoặc lỗi ràng buộc UNIQUE.
 */
@Component
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final NewsRepository newsRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      CategoryRepository categoryRepository,
                      NewsRepository newsRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.newsRepository = newsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("=== Bắt đầu kiểm tra và nạp dữ liệu mẫu (Stage 2) ===");

        // 1. Seed Users (Admin & Staff)
        User admin = seedUserIfNotFound(
                "Admin",
                "Admin",
                User.ROLE_ADMIN,
                User.STATUS_ACTIVE
        );

        User staff = seedUserIfNotFound(
                "Staff01",
                "Staff@123",
                User.ROLE_STAFF,
                User.STATUS_ACTIVE
        );

        // 2. Seed Categories
        Category catTech = seedCategoryIfNotFound(
                "Công nghệ & Đổi mới",
                "Chuyên mục tin tức về công nghệ số, trí tuệ nhân tạo và khởi nghiệp sáng tạo.",
                Category.STATUS_ACTIVE
        );

        Category catStudentLife = seedCategoryIfNotFound(
                "Đời sống Sinh viên",
                "Các hoạt động câu lạc bộ, sự kiện văn hóa nghệ thuật và phong trào tình nguyện.",
                Category.STATUS_ACTIVE
        );

        Category catAcademic = seedCategoryIfNotFound(
                "Thông báo Học vụ",
                "Lịch thi, quy chế học tập, hướng dẫn đồ án và chương trình học bổng.",
                Category.STATUS_ACTIVE
        );

        // 3. Seed News
        seedNewsIfNotFound(
                "FPT University tổ chức triển lãm công nghệ TechDay 2026",
                "Triển lãm TechDay 2026 quy tụ hơn 50 dự án sáng tạo từ sinh viên và doanh nghiệp công nghệ hàng đầu, mang đến những trải nghiệm thực tế về AI, Cloud và IoT.",
                catTech,
                admin,
                News.STATUS_ACTIVE
        );

        seedNewsIfNotFound(
                "Khai mạc giải bóng đá sinh viên thường niên FPT Champions League",
                "Hơn 32 đội bóng đại diện cho các khối ngành đã chính thức bước vào tranh tài tại mùa giải năm nay với tinh thần thể thao nhiệt huyết.",
                catStudentLife,
                staff,
                News.STATUS_ACTIVE
        );

        seedNewsIfNotFound(
                "Thông báo lịch đăng ký môn học và xét học bổng học kỳ tiếp theo",
                "Phòng Đào tạo thông báo thời gian mở cổng đăng ký học phần trên hệ thống và tiêu chuẩn xét duyệt học bổng khuyến học cho sinh viên đạt điểm xuất sắc.",
                catAcademic,
                admin,
                News.STATUS_ACTIVE
        );

        log.info("=== Hoàn tất nạp dữ liệu mẫu Stage 2 thành công ===");
    }

    private User seedUserIfNotFound(String username, String rawPassword, int role, int status) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            String hashedPassword = passwordEncoder.encode(rawPassword);
            User newUser = new User(username, hashedPassword, role, status);
            User saved = userRepository.save(newUser);
            User result = saved != null ? saved : newUser;
            log.info("Đã seed tài khoản: '{}' (Role: {}, Password đã mã hóa BCrypt: {}...)",
                    username, role == User.ROLE_ADMIN ? "Admin" : "Staff", hashedPassword.substring(0, 15));
            return result;
        });
    }

    private Category seedCategoryIfNotFound(String name, String description, int status) {
        return categoryRepository.findByName(name).orElseGet(() -> {
            Category newCategory = new Category(name, description, status);
            Category saved = categoryRepository.save(newCategory);
            Category result = saved != null ? saved : newCategory;
            log.info("Đã seed danh mục: '{}' (Status: {})", name, status);
            return result;
        });
    }

    private void seedNewsIfNotFound(String title, String content, Category category, User createdBy, int status) {
        if (!newsRepository.existsByTitle(title)) {
            News news = new News(title, content, category, createdBy, status);
            newsRepository.save(news);
            String catName = category != null ? category.getName() : "N/A";
            String authorName = createdBy != null ? createdBy.getUsername() : "N/A";
            log.info("Đã seed tin tức: '{}' (Chuyên mục: {}, Người tạo: {})", title, catName, authorName);
        }
    }
}
