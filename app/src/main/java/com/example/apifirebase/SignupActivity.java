package com.example.apifirebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apifirebase.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText edtEmail, edtPass;
    private Button btnSignup;
    private TextView tvGoToLogin;

    // Khai báo Firebase Auth và Database
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Khởi tạo Auth và Database Reference
        Auth_Database();
        
        //Ánh xạ
        AnhXa();

        btnSignup.setOnClickListener(v -> registerUser());

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });

    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPass.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            edtPass.setError("Mật khẩu phải từ 6 ký tự");
            return;
        }

        // 2. Tạo tài khoản trên Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký Auth thành công -> Lưu thông tin vào Database
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserToDatabase(firebaseUser);
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(FirebaseUser firebaseUser) {
        String uid = firebaseUser.getUid();
        String email = firebaseUser.getEmail();

        // Tạo đối tượng UserModel
        // Tạm thời để FullName là email, sau này bạn có thể thêm ô nhập tên riêng
        UserModel newUser = new UserModel(uid, email, email);

        // Đẩy lên Firebase: Users -> [uid] -> {thông tin}
        mDatabase.child(uid).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
                        // Chuyển về màn hình Login hoặc Main
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Lỗi lưu database: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void Auth_Database(){
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users"); // Trỏ vào node "Users"
    }

    private void AnhXa(){
        edtEmail = findViewById(R.id.edtEmailSignup);
        edtPass = findViewById(R.id.edtPassSignup);
        btnSignup = findViewById(R.id.btnSignup);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
    }
}