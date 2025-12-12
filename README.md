# 💬 Real-time Customer Support Chat (Android + Node.js)

Dự án xây dựng hệ thống Chat Hỗ trợ khách hàng thời gian thực sử dụng **Socket.IO**.
- **Client:** Ứng dụng Android (Java).
- **Backend:** Node.js (Express + Socket.IO).
- **Dashboard:** Giao diện Web đơn giản cho Manager.

---

## 🛠️ Yêu cầu cài đặt (Prerequisites)

1.  **Node.js:** Đã cài đặt trên máy tính.
2.  **Mạng Wifi:** Laptop (chạy Server) và Điện thoại (chạy App) phải kết nối **cùng một mạng Wifi**.
3.  **Android Studio:** Để build ứng dụng.

---

## 🚀 Hướng dẫn chạy Server (Backend)

Server nằm trong thư mục riêng tên là `ServerChat`.

1.  Mở Terminal/CMD tại thư mục `ServerChat`.
2.  Cài đặt thư viện (chỉ chạy lần đầu):
    ```bash
    npm install express socket.io cors nodemon
    ```
3.  Khởi động Server:
    ```bash
    npx nodemon index.js
    ```
    ✅ Server chạy thành công khi hiện: `SERVER ĐANG CHẠY TRÊN CỔNG 3000...`

---

## 📱 Hướng dẫn chạy App (Android)

### ⚠️ Quan trọng: Cấu hình địa chỉ IP
Do chạy local, mỗi khi đổi Wifi, IP của máy tính sẽ thay đổi. Cần cập nhật IP này vào App.

1.  Trên máy tính, mở CMD gõ `ipconfig` -> Lấy địa chỉ **IPv4** (VD: `192.168.1.15`).
2.  Mở file `Constant.java` trong Android Studio.
3.  Cập nhật biến `URL_SERVER`:
    ```java
    // Lưu ý: Viết liền, KHÔNG có dấu cách sau http://
    public static final String URL_SERVER = "[http://192.168.1.15:3000](http://192.168.1.15:3000)";
    ```
4.  Nhấn nút **Run ▶** để cài app vào điện thoại.

---

## 🧪 Kịch bản Test

1.  **Khách hàng (Android):**
    - Mở App -> Bấm vào icon Chat.
    - App tự sinh ID khách (VD: `guest_abc...`).
    - Nhắn tin: "Chào shop".

2.  **Quản lý (Web Browser):**
    - Mở file `test_manager.html` trong thư mục `ServerChat`.
    - Lấy ID khách (xem trong Logcat Android hoặc Terminal Server).
    - Nhập ID vào ô -> Bấm "Vào phòng".
    - Nhắn trả lời: "Shop nghe đây".

---

## ⚠️ Khắc phục lỗi thường gặp

- **Lỗi kết nối (Connection Error):** Kiểm tra lại IP trong `Constant.java` và đảm bảo tắt Firewall trên Windows.
- **Lỗi không nhận tin nhắn:** Kiểm tra xem ID nhập vào phía Manager có bị thừa dấu cách (space) không.
- **Mất tin nhắn cũ:** Server hiện tại lưu tin nhắn trên RAM, tắt Server sẽ mất dữ liệu.

---
