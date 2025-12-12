// === PHẦN 1: KHỞI TẠO SERVER ===
const express = require("express");
const app = express();
const http = require("http");
const cors = require("cors");
const { Server } = require("socket.io");
let chatHistory = {};

app.use(cors()); // Cho phép mọi kết nối (Android, Web)

const server = http.createServer(app);

// Cấu hình Socket.IO
const io = new Server(server, {
  cors: {
    origin: "*", // Dấu * nghĩa là chấp nhận kết nối từ bất kỳ IP nào (quan trọng khi test Android)
    methods: ["GET", "POST"],
  },
});

// === PHẦN 2: LOGIC XỬ LÝ CHAT ===
io.on("connection", (socket) => {
  // Khi có một thiết bị kết nối vào (Android hoặc Web Manager)
  console.log(`⚡ User connected: ${socket.id}`);

  // 1. Sự kiện: Tham gia phòng chat
  // Android gửi: socket.emit("join_room", "guest_12345");
  socket.on("join_room", (data) => {
    socket.join(data); // Cho socket này vào phòng tên là "data"
    console.log(`User ID: ${socket.id} đã vào phòng: ${data}`);

    // --- THÊM ĐOẠN NÀY: Gửi lại lịch sử chat cũ cho người vừa vào ---
        if (chatHistory[data]) {
            // Gửi sự kiện 'load_history' kèm theo danh sách tin nhắn cũ
            socket.emit("load_history", chatHistory[data]);
        }

    // Nếu là Guest, có thể bắn thông báo cho Manager biết (tùy chọn)
    if (data.toString().startsWith("guest_")) {
        console.log(">>> KHÁCH VÃNG LAI CẦN HỖ TRỢ!");
    }
  });

  // 2. Sự kiện: Gửi tin nhắn
  // Android gửi: socket.emit("send_message", {room: "...", content: "..."});
  socket.on("send_message", (data) => {
    console.log("📩 Nhận tin nhắn:", data);

    // Gửi tin nhắn này cho TẤT CẢ mọi người trong phòng đó (trừ người gửi)
    // Để bên kia (Manager) nhận được
    socket.to(data.room).emit("receive_message", data);
  });

  // 3. Sự kiện: Ngắt kết nối
  socket.on("disconnect", () => {
    console.log("User Disconnected", socket.id);
  });
});

// === PHẦN 3: CHẠY SERVER ===
// Server lắng nghe ở port 3000
// IP của máy bạn là gì thì Android sẽ gọi vào IP đó:3000
server.listen(3000, () => {
  console.log("SERVER ĐANG CHẠY TRÊN CỔNG 3000...");
});