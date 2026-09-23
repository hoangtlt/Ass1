# FUNewsManagementSystem — Kế hoạch triển khai theo stage

> Tài liệu lập kế hoạch cho **SBA301 Assignment 01 – Working with ReactJS application**. Đây là kế hoạch triển khai, chưa phải hướng dẫn chạy một bản source đã hoàn thành. Cập nhật phần hướng dẫn chạy và kết quả test thực tế khi bắt đầu viết project.

## 1. Mục tiêu và phạm vi

Xây dựng giao diện quản trị FUNewsManagementSystem bằng **ReactJS + Vite**, backend **Spring Boot + Spring Security** và dữ liệu trong **SQL Server**. Người dùng đăng nhập, xem Dashboard, quản lý Category, News, Users/Account và xem Settings. Ba trang Category, News và Users đều có **Read, Create, Update, Delete, Search**. Create/Update mở popup dialog; Delete có bước xác nhận.

### Bắt buộc theo tài liệu

- Thư mục project đặt tên theo quy ước `StudentName_ClassCode`.
- ReactJS + Vite; có Login page, đăng nhập bằng `Admin/Admin` vào admin page, thông báo khi thông tin sai.
- Header, logo tạo bằng AI, layout quản trị và menu đủ `Dashboard / Category / News / Users / Settings`.
- Category, News, Users/Account: CRUD + Search; Create/Update bằng dialog; Delete có confirmation và Cancel không xóa.
- Role tham chiếu: `1 = Admin`, `2 = Staff`; status tham chiếu của Category/News: `1 = Active`, `0 = Inactive`.
- Có README, kiểm thử và bằng chứng giải thích/kiểm chứng code theo rubric.

### Lựa chọn triển khai của dự án

- Tự xây giao diện vì hiện chưa được cấp HTML/CSS template. Nếu giảng viên cung cấp template về sau, đối chiếu và cập nhật giao diện theo yêu cầu lớp.
- Dùng REST API của Spring Boot thay cho mock data; SQL Server lưu dữ liệu qua các lần tải lại trang.
- Dùng Spring Security để xác thực và kiểm soát truy cập trên backend. Dự kiến dùng session; không thêm JWT nếu chưa có yêu cầu.
- Settings giới hạn ở xem thông tin tài khoản cá nhân và đổi mật khẩu.
- Backend kiểm tra quan hệ trước khi xóa; React chỉ gửi yêu cầu xóa sau khi người dùng xác nhận.

### Cần xác nhận trước khi hoàn tất phân quyền và quy tắc lịch sử

1. **Quyền Staff:** PDF yêu cầu role Admin/Staff nhưng không quy định đầy đủ quyền của Staff. Đề xuất đang chờ xác nhận: Admin có đầy đủ quyền; Staff đăng nhập, xem Dashboard, quản lý Category/News, xem và đổi mật khẩu của mình; Staff không vào Users. Đây là **đề xuất**, không được trình bày như yêu cầu bắt buộc của PDF.
2. **“Lịch sử” khi xóa:** Kế hoạch hiện hiểu là bản ghi đang được dữ liệu khác tham chiếu (Category có News; User đã tạo News). PDF không định nghĩa bảng lịch sử/audit. Nếu cần lưu lịch sử thao tác độc lập, phải bổ sung thiết kế và quy tắc xóa trước khi code.

## 2. Kiến trúc và dữ liệu

Luồng chính: **React → HTTP/JSON → Spring Boot Controller → Service → Repository → SQL Server**. State của dialog, form và từ khóa tìm kiếm ở React; dữ liệu nghiệp vụ gốc ở SQL Server. UI và backend đều kiểm tra dữ liệu, trong đó backend là nơi quyết định hợp lệ và kiểm tra quyền.

```text
StudentName_ClassCode/
├── frontend/      # ReactJS + Vite
├── backend/       # Spring Boot + Spring Security + REST API
└── README.md      # Hướng dẫn chạy, tài khoản test, chức năng, giới hạn và test
```

| Bảng | Trường tối thiểu | Quy tắc |
| --- | --- | --- |
| `users` | `id`, `username`, `password_hash`, `role`; `status` nếu sử dụng | Username duy nhất; `role` ánh xạ 1/2; không trả mật khẩu/hash trong API danh sách. Seed tài khoản `Admin/Admin` với mật khẩu đã hash. |
| `categories` | `id`, `name`, `status` | Tên không rỗng; status 0/1. |
| `news` | `id`, `title`, `content`, `category_id`, `created_by`, `status` | Category và người tạo phải tồn tại; người tạo lấy từ phiên đăng nhập khi Create; status 0/1. |

`tags` chỉ được tài liệu đề cập theo điều kiện “nếu có”; chưa đưa vào phạm vi bắt buộc. Tên bảng/cột và endpoint có thể điều chỉnh khi thiết kế code, nhưng phải giữ nguyên chức năng và quan hệ.

| Nhóm API dự kiến | Chức năng |
| --- | --- |
| `/api/auth/...` | Login, logout, lấy người dùng hiện tại. |
| `/api/categories` và `/api/categories/{id}` | Danh sách/tìm kiếm, tạo, sửa, xóa Category. |
| `/api/news` và `/api/news/{id}` | Danh sách/tìm kiếm, tạo, sửa, xóa News. |
| `/api/users` và `/api/users/{id}` | Danh sách/tìm kiếm, tạo, sửa, xóa User. |
| `/api/dashboard/...`, `/api/settings/...` | Số liệu tổng quan; thông tin cá nhân và đổi mật khẩu. |

## 3. Stage triển khai

Làm theo thứ tự dưới đây. Mỗi stage có một **điều kiện hoàn thành** để kiểm tra trước khi chuyển sang stage kế tiếp. Không ghi “hoàn thành” khi mới có giao diện nhưng API/SQL Server chưa hoạt động.

### Stage 0 — Phân tích yêu cầu và chốt thiết kế

**Chức năng/việc cần làm**

1. Lập bảng R01–R12: yêu cầu → màn hình → API/dữ liệu → test chứng minh.
2. Phác Login, Dashboard, Category, News, Users, Settings; Header, Sidebar, dialog form và dialog xác nhận.
3. Chốt trường form, validation và field tìm kiếm: Category theo name; News tối thiểu theo title; User theo username.
4. Vẽ quan hệ News → Category, News → User; thống nhất quy tắc xóa và cách hiển thị lỗi.
5. Chốt ma trận quyền Admin/Staff và ý nghĩa “lịch sử” ở mục **Cần xác nhận**.

**Đầu ra:** Bảng yêu cầu, phác giao diện, sơ đồ dữ liệu và quyết định quyền/xóa đã ghi rõ. **Qua stage khi:** giải thích được input, xử lý và output của từng chức năng bắt buộc.

### Stage 1 — Khởi tạo React và Spring Boot

**Chức năng/việc cần làm**

1. Tạo thư mục project đúng quy ước, `frontend/` bằng ReactJS + Vite và `backend/` bằng Spring Boot + Maven.
2. Chuẩn bị cấu trúc thư mục rõ ràng, `.gitignore`, dependency và file cấu hình mẫu không chứa secret.
3. Tạo endpoint kiểm tra backend như `GET /api/health`; React gọi endpoint này trong môi trường phát triển.
4. Viết README ban đầu với lệnh cài đặt và chạy cả hai ứng dụng.
5. Chưa buộc ứng dụng khởi động phải có database ở stage này; nếu khai báo sẵn JPA/SQL Server dependency, cấu hình để khởi động được trước Stage 2.

**Đầu ra:** `npm run dev` chạy frontend; Spring Boot chạy backend; React hiển thị được phản hồi từ `/api/health`; frontend build và backend test/build cơ bản thành công. **Chưa làm Login hoặc CRUD.**

### Stage 2 — SQL Server, bảng và dữ liệu mẫu

**Chức năng/việc cần làm**

1. Tạo database, ba bảng và ràng buộc unique/foreign key cần thiết.
2. Cấu hình backend kết nối SQL Server bằng thông tin môi trường, không commit mật khẩu thật.
3. Tạo Entity và Repository; seed Admin `Admin/Admin`, vài Category và News tham chiếu hợp lệ.
4. Mã hóa mật khẩu Admin khi lưu; thống nhất cách ánh xạ `role` 1/2 và `status` 0/1.

**Đầu ra:** Backend đọc được dữ liệu mẫu; bản ghi News không tham chiếu id không tồn tại; restart ứng dụng không mất dữ liệu. **Qua stage khi:** chỉ được vị trí database, bảng, quan hệ và dữ liệu seed.

### Stage 3 — Backend nền tảng và lỗi API

**Chức năng/việc cần làm**

1. Tách Controller / Service / Repository / DTO; chỉ đưa dữ liệu cần thiết ra API.
2. Dùng validation cho trường bắt buộc, giá trị role/status và các quy tắc nghiệp vụ.
3. Thống nhất phản hồi lỗi: form sai, id không tồn tại, trùng username, truy cập bị cấm, xóa bị chặn.
4. Thử API đọc dữ liệu SQL Server bằng công cụ gọi HTTP trước khi nối toàn bộ UI.

**Đầu ra:** Có JSON dữ liệu thật và lỗi có thông báo rõ; React có thể dựa vào phản hồi để hiện thông báo. **Qua stage khi:** đọc được một danh sách từ API và phân biệt được lỗi validation với lỗi không tìm thấy.

### Stage 4 — Spring Security, login và logout

**Chức năng/việc cần làm**

1. Đăng nhập bằng tài khoản User trong SQL Server; `Admin/Admin` phải thành công.
2. Tạo cơ chế session, endpoint lấy tài khoản hiện tại và logout; thống nhất cách React gửi cookie/CSRF khi cần.
3. Bảo vệ API trên backend theo ma trận quyền đã chốt; chưa đăng nhập hoặc sai role phải bị từ chối ngay cả khi gọi API trực tiếp.
4. Nếu dùng `status` cho User, quy định tài khoản Inactive không đăng nhập được.

**Đầu ra:** Login đúng/sai, logout và kiểm tra quyền API hoạt động; mật khẩu không xuất hiện trong JSON response. **Qua stage khi:** không thể truy cập API được bảo vệ bằng cách bỏ qua UI.

### Stage 5 — Login React, layout và navigation

**Chức năng/việc cần làm**

1. Login page có controlled inputs, báo lỗi rỗng/sai và chuyển vào Dashboard khi thành công.
2. Tự thiết kế Header có logo AI, tên hệ thống, tài khoản hiện tại và Logout.
3. Sidebar có đủ năm mục: Dashboard, Category, News, Users, Settings; chuyển view và tô mục đang chọn.
4. Các page dùng chung AdminLayout; chưa đăng nhập thì quay về Login; tải lại trang đọc lại trạng thái phiên.
5. Hiển thị/ẩn menu theo quyền **đã chốt**, đồng thời luôn giữ kiểm tra quyền ở backend.

**Đầu ra:** Demo được `mở app → login → xem Dashboard → chuyển menu → logout`; login sai không vào admin; Header/layout không phải viết lại ở từng trang.

### Stage 6 — Category CRUD + Search

| Chức năng | Việc làm và điều kiện hoàn thành |
| --- | --- |
| Read | API lấy danh sách; UI hiển thị name/status và thông báo khi danh sách rỗng. |
| Create | Add mở dialog rỗng; validate name/status; lưu SQL Server; thành công thì đóng dialog và cập nhật bảng. |
| Update | Edit mở dialog điền dữ liệu bản ghi đã chọn; lưu đúng `id`, không cập nhật nhầm dòng. |
| Delete | Hiện confirmation; Cancel không gửi DELETE; Confirm gọi API; từ chối nếu đã có News sử dụng. |
| Search | Tìm theo name; xử lý khoảng trắng/chữ hoa thường; có no-result state; clear search trở về danh sách đầy đủ. |

**Đầu ra:** Category hoạt động trọn luồng React → API → SQL Server; sau reload dữ liệu vẫn đúng; lỗi không xóa được hiển thị rõ. **Đây là mốc tích hợp đầu tiên**, hoàn thành trước khi làm News.

### Stage 7 — News CRUD + Search

| Chức năng | Việc làm và điều kiện hoàn thành |
| --- | --- |
| Read | Hiển thị title, Category, người tạo, status; có empty state. |
| Create | Add dialog nhập title/content, chọn Category, status; backend kiểm tra Category tồn tại và lấy `createdBy` từ tài khoản đăng nhập. |
| Update | Edit dialog điền dữ liệu đúng News; kiểm tra Category mới; chỉ cập nhật đúng `id`. |
| Delete | Confirmation với Cancel/Confirm; backend kiểm tra quan hệ nếu sau này News được dữ liệu khác tham chiếu. |
| Search | Tìm tối thiểu theo title; có hit/miss/clear và không ghi đè dữ liệu gốc. |

**Đầu ra:** Tạo được News gắn Category thật và người tạo thật; CRUD/Search hoạt động sau reload; id Category sai bị từ chối.

### Stage 8 — Users/Account CRUD + Search

| Chức năng | Việc làm và điều kiện hoàn thành |
| --- | --- |
| Read | Hiển thị username, nhãn Admin/Staff và status nếu có; không hiển thị mật khẩu/hash. |
| Create | Add dialog nhập username/password/role/status; username duy nhất; password được hash. |
| Update | Edit đúng User; chỉ đổi password khi người quản lý nhập mật khẩu mới; không xóa mật khẩu cũ khi trường này trống. |
| Delete | Confirmation; backend từ chối nếu User đã tạo News, đang là tài khoản đăng nhập hoặc là Admin cuối cùng. |
| Search | Tìm theo username; có no-result state và clear search. |
| Quyền | Bảo vệ API Users theo ma trận quyền đã chốt; role lưu trong DB phải khớp role của Spring Security. |

**Đầu ra:** Tạo/sửa/tìm/xóa User theo quy tắc; tài khoản mới có role phù hợp; gọi API Users trái quyền bị từ chối.

### Stage 9 — Dashboard và Settings

**Dashboard:** Hiển thị số Category, News, Users và số Active/Inactive nếu có. Số liệu lấy từ backend, thay đổi khi dữ liệu nghiệp vụ thay đổi.

**Settings:** Xem username và role của chính mình; đổi mật khẩu bằng mật khẩu hiện tại, mật khẩu mới và xác nhận. Validation cho mật khẩu hiện tại sai hoặc xác nhận không khớp. Không thêm cấu hình hệ thống chưa được yêu cầu.

**Đầu ra:** Cả năm mục menu đều có nội dung; số Dashboard phù hợp database; đổi mật khẩu thành công thì đăng nhập được bằng mật khẩu mới.

### Stage 10 — Hoàn thiện tích hợp và quy tắc xóa

**Chức năng/việc cần làm**

1. Đồng bộ trạng thái sau Create/Update/Delete: danh sách, search và Dashboard phản ánh dữ liệu mới.
2. React hiển thị trạng thái đang tải, lỗi API và phản hồi validation; dialog không tự đóng khi lưu thất bại.
3. Category chỉ được xóa khi không có News tham chiếu, kể cả News Inactive.
4. User không được xóa khi đã tạo News; không được xóa chính mình hoặc Admin cuối cùng.
5. Quy tắc xóa chạy trên backend; nếu database từ chối bởi foreign key, trả thông báo dễ hiểu.
6. Nếu quyết định làm bảng lịch sử/audit, cập nhật riêng mô hình dữ liệu, quy tắc xóa và test trước khi triển khai; **chưa coi audit là phạm vi đã chốt**.

**Đầu ra:** Các quy tắc xóa và lỗi hoạt động nhất quán từ UI đến database; không để quan hệ mồ côi; thao tác thất bại không làm giao diện hiển thị thành công giả.

### Stage 11 — Kiểm thử và sửa lỗi

**Test matrix tối thiểu**

- Login: rỗng, sai username/password, đúng `Admin/Admin`, logout, truy cập khi chưa login.
- Layout/menu: đủ năm mục, chuyển đúng page, không reload ngoài ý muốn, đúng menu theo quyền.
- Với **mỗi** Category, News, Users: Read; Create hợp lệ/thiếu trường; Update đúng bản ghi; Delete Cancel/Confirm; Search có kết quả/không có kết quả; danh sách rỗng.
- Dialog: Create mở form rỗng; Edit hai bản ghi liên tiếp không giữ state sai; Cancel không ghi dữ liệu.
- Dữ liệu: News chọn Category hợp lệ; role 1/2 và status 0/1 hiển thị đúng; tải lại trang vẫn có dữ liệu.
- Quan hệ và quyền: xóa Category/User đang được sử dụng bị chặn; Staff và Admin gọi API đúng quyền đã chốt; lỗi API được hiển thị.

Ghi với mỗi case: **thao tác, kết quả mong đợi, kết quả thực tế, Pass/Fail, bằng chứng**. Nếu phát hiện bug, ghi triệu chứng → nguyên nhân → sửa → test lại và kiểm tra chức năng liên quan.

**Đầu ra:** Test matrix có kết quả thực tế, ảnh/log khi cần, 2–3 debug notes từ lỗi thật; chạy được demo liền mạch từ Login qua CRUD/Search và Logout.

### Stage 12 — README hoàn chỉnh, nộp và chuẩn bị bảo vệ

**Việc cần làm**

1. Cập nhật README này thành README chạy project: yêu cầu môi trường, tạo database, cấu hình không chứa secret, lệnh backend/frontend, tài khoản test, chức năng, giới hạn và lỗi thường gặp.
2. Chuẩn bị source/zip hoặc GitHub theo quy trình lớp; ảnh hoặc video chức năng chính nếu giảng viên yêu cầu; test matrix và AI usage log nếu có/yêu cầu.
3. Tập giải thích ba luồng: login (input → Spring Security → session → UI), CRUD (dialog → API → database → UI), search (keyword → danh sách kết quả).
4. Luyện một yêu cầu thay đổi nhỏ, ví dụ thêm filter Active/Inactive; nêu file bị ảnh hưởng và test lại chức năng cũ.

**Đầu ra:** Người khác chạy được đúng phiên bản nộp theo README; bạn demo và chỉ được code xử lý từng yêu cầu, giải thích được lỗi và tự làm một thay đổi nhỏ.

## 4. Mốc hoàn thành nhanh

| Mốc | Kết quả có thể demo |
| --- | --- |
| Sau Stage 1 | Hai ứng dụng chạy, React gọi được health API. |
| Sau Stage 5 | Đăng nhập, logout, layout và menu hoạt động. |
| Sau Stage 6 | Category CRUD + Search thật với SQL Server. |
| Sau Stage 8 | Cả Category, News, Users có CRUD + Search và đúng quan hệ/quyền đã chốt. |
| Sau Stage 10 | Dashboard/Settings và quy tắc xóa tích hợp hoàn chỉnh. |
| Sau Stage 12 | Có bài chạy được, test evidence, hướng dẫn chạy và khả năng bảo vệ bài. |

## 5. Nguyên tắc ưu tiên

Hoàn thành đúng core requirement và test trước khi thêm chart, pagination, dark mode, tags hoặc deployment. PDF chấm cả **hiểu yêu cầu, thiết kế, triển khai, kiểm thử, debug, giải thích code, xử lý thay đổi và sử dụng AI có kiểm chứng**; mỗi stage nên có commit và bằng chứng tương ứng.
