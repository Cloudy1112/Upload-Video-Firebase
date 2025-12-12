package com.example.apifirebase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apifirebase.adapter.ChatAdapter;
import com.example.apifirebase.model.MessageModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.UUID;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatActivity extends AppCompatActivity {
    private String myuserid;
    private Socket socket;
    private Button btnsend;
    private RecyclerView recyclerView;
    private EditText edtInput;
    private String URL = new Constant().URL_Server;
    private ChatAdapter chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        myuserid = getGuestId();
        AnhXaSetAdapter();

        connectSocket();

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void connectSocket() {
        try {
            socket = IO.socket(URL);
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, args -> {
                // Gửi yêu cầu vào phòng
                socket.emit("join_room", myuserid);
            });

            // === 1. LẮNG NGHE LỊCH SỬ CHAT (Sự kiện mới) ===
            socket.on("load_history", args -> {
                if (args[0] != null) {
                    runOnUiThread(() -> {
                        try {
                            // Dữ liệu trả về là một Danh sách (JSONArray)
                            JSONArray listMessages = (JSONArray) args[0];

                            // Xóa tin nhắn cũ trên màn hình (để tránh bị trùng lặp)
                            // Bạn cần thêm hàm clear() vào Adapter hoặc tạo list mới
                            // Ở đây mình giả định bạn tạo list mới

                            for (int i = 0; i < listMessages.length(); i++) {
                                JSONObject msgObj = listMessages.getJSONObject(i);
                                String content = msgObj.getString("content");
                                String sender = msgObj.getString("sender");

                                // Thêm từng tin nhắn vào giao diện
                                chatAdapter.addMessage(new MessageModel(content, sender));
                            }

                            // Cuộn xuống dòng cuối cùng
                            recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });

            // === 2. LẮNG NGHE TIN NHẮN MỚI (Code cũ giữ nguyên) ===
            socket.on("receive_message", args -> {
                runOnUiThread(() -> {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String content = data.getString("content");
                        String sender = data.getString("sender");

                        chatAdapter.addMessage(new MessageModel(content, sender));
                        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String content = edtInput.getText().toString().trim();
        if (content.isEmpty()) return;

        // Đóng gói tin nhắn JSON
        JSONObject data = new JSONObject();
        try {
            data.put("room", myuserid); // Gửi vào phòng của chính mình
            data.put("content", content);
            data.put("sender", myuserid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Gửi lên Server
        socket.emit("send_message", data);

        // Hiển thị luôn lên màn hình mình (cho nhanh)
        chatAdapter.addMessage(new MessageModel(content, myuserid));
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

        // Xóa ô nhập
        edtInput.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cực kỳ quan trọng: Ngắt kết nối khi thoát màn hình để tránh tốn pin/tràn bộ nhớ
        if (socket != null) {
            socket.disconnect();
            socket.off(); // Gỡ bỏ các lắng nghe
        }
    }
    private void AnhXaSetAdapter() {
        btnsend = findViewById(R.id.btnSendChat);
        recyclerView = findViewById(R.id.recyclerViewChat);
        edtInput = findViewById(R.id.edtChatInput);

        chatAdapter = new ChatAdapter(myuserid);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
    }

    private String getGuestId() {
        // 1. Mở bộ nhớ đệm (SharedPreferences) của App
        SharedPreferences prefs = getSharedPreferences("CHAT_PREFS", MODE_PRIVATE);

        // 2. Lấy ID đã lưu (nếu có)
        String savedId = prefs.getString("GUEST_ID", null);

        // 3. Nếu chưa có (Lần đầu mở app chat) -> Tạo ID ngẫu nhiên
        if (savedId == null) {
            // UUID tạo ra một chuỗi không trùng lặp
            savedId = "guest_" + UUID.randomUUID().toString();

            // Lưu lại để lần sau dùng tiếp
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("GUEST_ID", savedId);
            editor.apply();
        }

        return savedId;
    }
}