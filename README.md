Link figma về đồ án  : https://www.figma.com/design/Ba0SdiExeOHRFGFhxh665c/Untitled?node-id=0-1&p=f&t=57RVqaNAqdWtAvDg-0

Đồ Án Hệ Thống Đặt Đồ Ăn Trực Tuyến - Food Ordering App (BurgerKing)

1. Giới thiệu
Hệ thống Food Ordering App là giải pháp đặt đồ ăn trực tuyến toàn diện, bao gồm Ứng dụng di động (Android) dành cho khách hàng và Website quản trị (Admin Dashboard) dành cho cửa hàng.
Hệ thống được vận hành bởi Backend Server mạnh mẽ (Node.js) triển khai trên nền tảng Render, giúp đồng bộ dữ liệu thời gian thực giữa khách hàng và nhà quản lý, đảm bảo quy trình gọi món và xử lý đơn hàng diễn ra thông suốt.

2. Tính Năng Chính Của Hệ Thống

A. Đối Với Khách Hàng (Android App)

🔹 Tài khoản & Hồ sơ: Đăng ký/Đăng nhập (Email, Google), Quên mật khẩu (OTP), Quản lý thông tin cá nhân, Avatar.

🔹 Đặt món: Xem menu trực quan, Tìm kiếm món ăn, Thêm vào giỏ hàng, Áp dụng Voucher.

🔹 Thanh toán & Đơn hàng: Đặt hàng nhanh chóng, Xem lịch sử đơn hàng, Theo dõi trạng thái đơn (Đang xử lý -> Đang giao -> Hoàn tất).

🔹 Thông báo: Nhận thông báo đẩy (Notification) khi có voucher giảm giá.

B. Đối Với Quản Trị Viên (Web Admin)

🔹 Dashboard (Tổng quan): Xem thống kê nhanh về số lượng đơn hàng, doanh thu trong ngày.


🔹 Quản lý Thực Đơn: Thêm món mới, Sửa giá/mô tả, Xóa món, Cập nhật hình ảnh món ăn.

🔹 Quản lý Đơn Hàng (Real-time):

   - Nhận đơn hàng mới ngay lập tức từ App.

   - Duyệt đơn (Chuyển trạng thái từ Chờ xử lý -> Đang giao).

   - Xem chi tiết từng đơn (Món gì, Khách nào, Địa chỉ, SĐT).

   - Chat với trực tiếp với khách hàng .
     
🔹 Quản lý Voucher: Tạo mã giảm giá, thiết lập hạn sử dụng và mức giảm.

3. Công Nghệ Sử Dụng

🔹 Mobile App (Client):

   - Ngôn ngữ: Kotlin
     
   - Framework UI: Jetpack Compose
     
   - Thư viện: Retrofit (Giao tiếp API), Coil (Load ảnh), Firebase SDK.

🔹 Backend Server & Web Admin:

   - Ngôn ngữ Server: Node.js (Express Framework).
     
   - Giao diện Web Admin: HTML/CSS/JavaScript (EJS/Handlebars View Engine).
     
   - Triển khai (Hosting): Server chạy trên Render (Cloud Hosting).

🔹 Cơ Sở Dữ Liệu & Dịch Vụ:

   - Firebase Firestore: Database chính (NoSQL) lưu trữ User, Món ăn, Đơn hàng.
     
   - Firebase Authentication: Xác thực người dùng.
     
   - Firebase Storage: Lưu trữ hình ảnh.
     
   - Firebase Cloud Messaging (FCM): Gửi thông báo đẩy.

4. Hướng Dẫn Cài Đặt Cơ Bản
   
- Mobile App: Cài file APK vào máy Android.
  
- Web Admin: Truy cập đường dẫn: https://foodapp-server-txfk.onrender.com/
  
- Link github để truy cập source code của server và web admin :https://github.com/quoctri1014/FoodApp-Server
