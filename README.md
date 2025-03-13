# Dự án ứng dụng To Do List
- Danh sách công việc
- Thêm mới
- Chỉnh sửa
- Xoá
- Đánh dấu hoàn thành
- Tìm kiếm
# Công nghệ sử dụng
- UI: Jetpack Compose
- Asynchronous and reactive: Kotlin Coroutines
- Dependency injection: Dagger Hilt
- Database: Room
# Kiến trúc dự án
- Common:
  - Ext - các hàm extention
  - DI - Dependency Injection Application
- Domain: TaskUseCase - cung cấp các hàm liên quan đến các tính năng của ứng dụng (thao tác với Task)
- Data:
  - Dao: TaskDao - Interface cầm các query tương ứng với các tính năng thêm sửa xoá TaskEntity
  - Entity: TaskEntity - định nghĩa class Task được lưu trữ trong Room database
  - AppDatabase: Khởi tạo database lưu trữ dữ liệu task
- Presentation:
  - Intent: Định nghĩa các thao tác người dùng tương tác với UI
    -  AddTask
    -  UpdateTask
    -  CheckTask (đánh dấu hoàn thành)
    -  DeleteTask
    -  SearchTask
    -  ToggleFinishedTasks (ẩn/hiện tasks đã hoàn thành)
    -  TogglePendingTasks (ẩn/hiện tasks chưa hoàn thành)
  - Model: Task - Định nghĩa 1 thành phần công việc dưới dạng Class
  - View:
    - Component: Các thành phần có khả năng tái sử dụng
    - State:
      - TaskState - Định nghĩa các thành phần trong trạng thái của màn hình
      - Resource - Phân loại các kiểu dữ liệu trả trong quá trình xử lý bất đồng bộ
    - Theme: Được hỗ trợ gen sẵn bởi project - cung cấp màu, kiểu văn bản và theme cho ứng dụng
    - MainActivity: Giao diện màn hình ứng dụng (1 activity duy nhất)
    - MainViewModel: Giữ trạng thái cho MainActivity, cho phép gọi đến các Intent tương ứng với thao tác ngươi dùng
# Demo

![video_2025-03-13_22-33-12 (1)](https://github.com/user-attachments/assets/20ae3311-6b24-46b5-b3b0-f626b36173d3)

