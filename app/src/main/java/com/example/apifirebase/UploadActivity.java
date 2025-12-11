package com.example.apifirebase;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class UploadActivity extends AppCompatActivity {
    // View
    private EditText edtTitle;
    private VideoView videoView;
    private Button btnUpload;
    private FloatingActionButton fabPick;
    private ProgressBar progressBar;
    private ImageView imgBack;

    // Data
    private Uri videoUri = null; // Lưu đường dẫn file video đã chọn

    // Permission constants
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Ánh xạ
        edtTitle = findViewById(R.id.edtTitle);
        videoView = findViewById(R.id.videoViewPreview);
        btnUpload = findViewById(R.id.btnUploadVideo);
        fabPick = findViewById(R.id.fabPickVideo);
        progressBar = findViewById(R.id.progressBarUpload);
        imgBack = findViewById(R.id.imgBack);

        // 1. Sự kiện nút Back
        imgBack.setOnClickListener(v -> finish());

        // 2. Sự kiện nút Chọn Video
        fabPick.setOnClickListener(v -> checkPermissionAndPickVideo());

        // 3. Sự kiện nút Upload
        btnUpload.setOnClickListener(v -> {
            String title = edtTitle.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            } else if (videoUri == null) {
                Toast.makeText(this, "Vui lòng chọn video trước", Toast.LENGTH_SHORT).show();
            } else {
                uploadVideoToFirebase(title);
            }
        });
    }

    // --- PHẦN 1: CHỌN VIDEO TỪ THƯ VIỆN ---
    private void checkPermissionAndPickVideo() {
        // Kiểm tra quyền tùy theo phiên bản Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ cần quyền READ_MEDIA_VIDEO
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
                    == PackageManager.PERMISSION_GRANTED) {
                pickVideoIntent();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_VIDEO}, STORAGE_PERMISSION_CODE);
            }
        } else {
            // Android cũ hơn cần quyền READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                pickVideoIntent();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }
    }

    // Xử lý kết quả xin quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickVideoIntent();
            } else {
                Toast.makeText(this, "Cần cấp quyền để lấy video", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickVideoIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        videoPickerLauncher.launch(intent);
    }

    // Nhận kết quả video trả về
    private final ActivityResultLauncher<Intent> videoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    videoUri = result.getData().getData();

                    // Hiển thị video lên VideoView để xem trước
                    videoView.setVideoURI(videoUri);
                    videoView.start();

                    // Ẩn nút chọn để nhìn cho rõ, hoặc để lại tùy bạn
                    // fabPick.setVisibility(View.GONE);
                    Toast.makeText(this, "Đã chọn video!", Toast.LENGTH_SHORT).show();
                }
            }
    );

    // --- PHẦN 2: UPLOAD LÊN FIREBASE ---

    private void uploadVideoToFirebase(String title) {
        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false); // Khóa nút bấm

        // Tạo tên file ngẫu nhiên dựa trên thời gian
        String timestamp = "" + System.currentTimeMillis();
        String filePathAndName = "Videos/video_" + timestamp;

        // Upload lên Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        storageReference.putFile(videoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Upload file thành công -> Lấy link Download
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadUri = uriTask.getResult();

                    if (uriTask.isSuccessful()) {
                        // Có link rồi -> Lưu thông tin vào Database
                        saveVideoInfoToDatabase(downloadUri.toString(), title, timestamp);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(UploadActivity.this, "Lỗi Upload: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveVideoInfoToDatabase(String downloadUrl, String title, String timestamp) {
        String uid = FirebaseAuth.getInstance().getUid(); // Lấy ID người đang đăng nhập

        // Tạo HashMap để đẩy dữ liệu
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("videoId", timestamp);
        hashMap.put("title", title);
        hashMap.put("timestamp", Long.parseLong(timestamp)); // Lưu dạng số để dễ sắp xếp
        hashMap.put("videoUrl", downloadUrl);
        hashMap.put("uploaderId", uid);
        hashMap.put("likeCount", 0);

        // Lưu vào node "Videos"
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Videos");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadActivity.this, "Đăng video thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình này lại
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                    Toast.makeText(UploadActivity.this, "Lỗi DB: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}