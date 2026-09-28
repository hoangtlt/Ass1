package com.fu.news;

import com.fu.news.repository.CategoryRepository;
import com.fu.news.repository.NewsRepository;
import com.fu.news.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Kiểm tra khởi động ApplicationContext cơ bản trong môi trường test độc lập
 * khi chưa có thông tin đăng nhập SQL Server thật trên máy test/CI.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration",
        "app.seeder.enabled=false"
})
class FUNewsManagementSystemApplicationTests {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CategoryRepository categoryRepository;

    @MockitoBean
    private NewsRepository newsRepository;

    @Test
    void contextLoads() {
        // ApplicationContext nạp thành công các Bean cốt lõi
    }
}
