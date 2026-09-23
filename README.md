# FUNewsManagementSystem — Stage 1: Khởi tạo Project

Dự án mẫu bài tập lớn môn **SBA301 (Assignment 01)** — Hệ thống quản lý tin tức trường đại học (FUNewsManagementSystem).

---

## 📌 Lưu ý về Đổi tên Thư mục (Naming Convention)

Theo yêu cầu của đề bài SBA301, thư mục gốc của project phải được đặt theo định dạng:
```
StudentName_ClassCode
```
*Hiện tại, project đang nằm trong thư mục:* `HoangTLT_SE1910`.

> [!IMPORTANT]
> **Nếu bạn cần đổi tên theo thông tin sinh viên của bạn:**
> 1. Đóng IDE và các terminal đang mở trong thư mục này.
> 2. Đổi tên thư mục `HoangTLT_SE1910` thành `[TênCủaBạn]_[MãLớp]` (Ví dụ: `NguyenVanA_SE1801`).
> 3. Mở lại terminal trong thư mục mới và tiếp tục làm việc bình thường. Toàn bộ cấu hình bên trong (Maven, Vite) đều sử dụng đường dẫn tương đối, không bị phụ thuộc vào tên thư mục gốc.

---

## 🛠️ Yêu cầu Môi trường (Prerequisites)

Trước khi chạy, máy tính của bạn cần cài đặt:
- **Java Development Kit (JDK):** Phiên bản **21 LTS** (hoặc cao hơn). Kiểm tra bằng `java -version`.
- **Apache Maven:** Phiên bản **3.9+** (hoặc sử dụng sẵn file Maven Wrapper `./mvnw` / `mvnw.cmd` đi kèm).
- **Node.js:** Phiên bản **20.x** hoặc **22.x+**. Kiểm tra bằng `node -v`.
- **npm:** Phiên bản **10.x+**. Kiểm tra bằng `npm -v`.

---

## 📂 Cấu trúc Thư mục Dự án

```text
HoangTLT_SE1910/
├── .gitignore                    # Cấu hình bỏ qua file cho Git (root, backend, frontend)
├── README.md                     # Hướng dẫn chi tiết dự án (file này)
│
├── backend/                      # Ứng dụng Backend Spring Boot (Java 21)
│   ├── mvnw & mvnw.cmd           # Maven Wrapper cho Windows và Linux/macOS
│   ├── .mvn/                     # Cấu hình Maven Wrapper
│   ├── pom.xml                   # Cấu hình Maven, dependencies và plugins
│   └── src/
│       ├── main/
│       │   ├── java/com/fu/news/
│       │   │   ├── FUNewsManagementSystemApplication.java   # Main class (exclude DB ở Stage 1)
│       │   │   ├── config/
│       │   │   │   └── WebConfig.java                       # Cấu hình CORS cho Frontend
│       │   │   ├── controller/
│       │   │   │   └── HealthController.java                # Endpoint GET /api/health
│       │   │   ├── dto/
│       │   │   │   └── HealthResponse.java                  # DTO trả về JSON sức khỏe hệ thống
│       │   │   ├── entity/                                  # Package sẵn sàng cho Stage 2
│       │   │   ├── repository/                              # Package sẵn sàng cho Stage 2
│       │   │   └── service/                                 # Package sẵn sàng cho Stage 2
│       │   └── resources/
│       │       └── application.properties                   # Cấu hình cổng 8080 & mẫu DB Stage 2
│       └── test/
│           └── java/com/fu/news/
│               ├── FUNewsManagementSystemApplicationTests.java
│               └── controller/
│                   └── HealthControllerTest.java
│
└── frontend/                     # Ứng dụng Frontend ReactJS + Vite (JavaScript / JSX)
    ├── package.json              # Khai báo dependencies (React 19, Vite 8)
    ├── vite.config.js            # Cấu hình Vite Dev Proxy (/api ➔ localhost:8080)
    ├── index.html                # File HTML chính với Google Fonts Plus Jakarta Sans
    └── src/
        ├── main.jsx              # Entry point React
        ├── App.jsx               # Giao diện chính hiển thị trạng thái Backend và thông tin
        ├── App.css               # CSS styling giao diện & các thẻ thông tin
        ├── index.css             # Design system (dark mode, biến màu, animation, typography)
        └── services/
            └── api.js            # Hàm gọi API /api/health và đo latency
```

---

## 🚀 Hướng dẫn Cài đặt & Khởi chạy

### 1. Khởi chạy Backend (Spring Boot)

Mở một cửa sổ Terminal (hoặc Command Prompt / PowerShell) và điều hướng vào thư mục `backend/`:

```powershell
cd backend
```

#### Cách 1: Sử dụng Maven cài sẵn trên máy
```powershell
# Biên dịch và kiểm tra
mvn test

# Chạy Backend
mvn spring-boot:run
```

#### Cách 2: Sử dụng Maven Wrapper đi kèm (không cần cài trước Maven)
- **Trên Windows:**
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```
- **Trên macOS / Linux:**
  ```bash
  ./mvnw spring-boot:run
  ```

> Backend sẽ khởi động và lắng nghe tại: **`http://localhost:8080`**  
> Kiểm tra thử API độc lập trên trình duyệt hoặc qua curl:
> ```bash
> curl http://localhost:8080/api/health
> ```
> Phản hồi mẫu:
> ```json
> {
>   "status": "UP",
>   "appName": "FUNewsManagementSystem",
>   "version": "1.0.0-STAGE1",
>   "timestamp": "2026-09-23T10:30:00.123456"
> }
> ```

---

### 2. Khởi chạy Frontend (React + Vite)

Mở một cửa sổ Terminal **mới khác** và điều hướng vào thư mục `frontend/`:

```powershell
cd frontend
```

#### Bước 2.1: Cài đặt thư viện (chỉ cần chạy lần đầu)
```powershell
npm install
```

#### Bước 2.2: Khởi chạy môi trường phát triển (Dev Server)
```powershell
npm run dev
```

Giao diện Web sẽ chạy tại: **`http://localhost:5173`**

Khi truy cập trang web:
- Frontend sẽ tự động gửi yêu cầu đến `/api/health`.
- Nhờ Vite Dev Proxy cấu hình trong `vite.config.js`, yêu cầu `/api/health` sẽ được chuyển tiếp tới `http://localhost:8080/api/health`.
- Giao diện sẽ hiển thị thẻ trạng thái màu xanh lá: **`KẾT NỐI THÀNH CÔNG (UP)`**, kèm dữ liệu JSON và độ trễ phản hồi (latency tính bằng ms).

#### Bước 2.3: Đóng gói kiểm tra (Production Build)
```powershell
npm run build
```

---

## 🔒 Cơ chế Độc lập trong Stage 1 (Chưa cần Database)

Trong Stage 1, theo đúng yêu cầu đề bài:
1. Dự án **chưa kết nối với SQL Server** và **không sử dụng database in-memory (H2)** để tránh tạo dữ liệu giả.
2. File `FUNewsManagementSystemApplication.java` được cấu hình loại trừ:
   ```java
   @SpringBootApplication(exclude = {
           DataSourceAutoConfiguration.class,
           DataSourceTransactionManagerAutoConfiguration.class,
           HibernateJpaAutoConfiguration.class
   })
   ```
   Nhờ đó, dù project có sẵn thư viện `spring-boot-starter-data-jpa` và `mssql-jdbc` cho Stage 2, backend vẫn khởi động an toàn và trả về API bình thường mà không bị crash do thiếu connection string.
3. Không lưu hard-code mật khẩu hoặc secret key trong source code.

---

## 🗺️ Lộ trình triển khai tiếp theo (Stage 2 Preview)

Khi chuyển sang **Stage 2**, các công việc tiếp theo bao gồm:
1. **Cơ sở dữ liệu:** Cài đặt SQL Server, tạo database `FUNewsManagement` và chạy script tạo bảng.
2. **Cấu hình Spring Boot:** Mở comment phần `spring.datasource.*` trong `backend/src/main/resources/application.properties` và bỏ thuộc tính `exclude = {...}` trong `FUNewsManagementSystemApplication.java`.
3. **Mô hình hóa dữ liệu (Entities):** Tạo các entity `Category`, `NewsArticle`, `SystemAccount`, `Tag` trong package `com.fu.news.entity`.
4. **Spring Data JPA Repositories:** Tạo các interface kế thừa `JpaRepository` trong `com.fu.news.repository`.
5. **Spring Security & Authentication:** Triển khai cơ chế phân quyền (Admin, Staff, Lecturer) và API Login.
6. **Frontend Integration:** Xây dựng màn hình Đăng nhập và các trang chức năng theo phân quyền.
#   A s s 1  
 