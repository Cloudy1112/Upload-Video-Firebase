package com.example.apifirebase;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {
    private Button btnChange;
    private TextView emailtxt, fullnametxt, videocounttxt, liketxt;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        AnhXa();

        LoadInfo();
        loadVideoCount();

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hien cua so dien thong tin
            }
        });

    }

    private void loadVideoCount() {
        liketxt.setText(0);
        videocounttxt.setText(0);
    }

    private void LoadInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            userRef.child("profilePic").get().addOnSuccessListener(snapshot -> {
                if (snapshot.exists()) {
                    String url = snapshot.getValue(String.class);
                    // Dùng Glide load ảnh
                    Glide.with(this).load(url).placeholder(R.mipmap.ic_launcher).into(image);
                }
            });
        }
    }

    private void AnhXa() {
        btnChange = findViewById(R.id.profile_btnchange);
        emailtxt = findViewById(R.id.profile_email);
        fullnametxt = findViewById(R.id.profile_fullname);
        liketxt = findViewById(R.id.profile_like);
        videocounttxt = findViewById(R.id.profile_uploadvideo);
    }
}