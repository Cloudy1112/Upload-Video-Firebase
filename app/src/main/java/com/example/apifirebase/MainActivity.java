package com.example.apifirebase;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.apifirebase.adapter.VideoAdapter;
import com.example.apifirebase.model.VideoModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.Intent;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    // Khai báo View
    private ViewPager2 viewPager2;
    private FloatingActionButton btnUploadNav;
    private CircleImageView imgMyAvatar;

    // Khai báo List và Adapter
    private List<VideoModel> videoList;
    private VideoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ View
        viewPager2 = findViewById(R.id.viewPager2);
        btnUploadNav = findViewById(R.id.btnUploadNav);
        imgMyAvatar = findViewById(R.id.imgMyAvatar);

        // 2. Thiết lập ViewPager2 (Video Shorts)
        videoList = new ArrayList<>();
        adapter = new VideoAdapter(videoList, this);
        viewPager2.setAdapter(adapter);

        // Load danh sách video từ Firebase
        loadVideosFromFirebase();

        // 3. Sự kiện Click nút Upload -> Mở màn hình Upload
        btnUploadNav.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, UploadActivity.class));
        });

        // 4. Sự kiện Click Avatar -> Mở màn hình Profile (để cập nhật ảnh/thống kê)
        imgMyAvatar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        // Load ảnh avatar của chính mình lên góc phải
        loadMyAvatar();


    }

    private void loadMyAvatar() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Lấy link ảnh từ Database Users (bạn cần code phần Profile để lưu ảnh trước đã)
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            userRef.child("profilePic").get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String url = snapshot.getValue(String.class);
                    // Dùng Glide load ảnh
                    Glide.with(this).load(url).placeholder(R.mipmap.ic_launcher).into(imgMyAvatar);
                }
            });
        }
    }

    private void loadVideosFromFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Videos");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    VideoModel video = data.getValue(VideoModel.class);
                    videoList.add(video);
                }
                Collections.reverse(videoList); // Đưa video mới nhất lên đầu
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi tải video", Toast.LENGTH_SHORT).show();
            }
        });
    }


}