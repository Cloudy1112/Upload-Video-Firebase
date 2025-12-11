package com.example.apifirebase.model;

public class UserModel {
    private String uid;         // User ID từ FirebaseAuth
    private String email;
    private String fullName;    // Tên hiển thị (nếu muốn mở rộng sau này)
    private String profilePic;  // Link ảnh đại diện (nếu muốn mở rộng)

    public UserModel() {
    }

    public UserModel(String uid, String email, String fullName) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
    }

    // Getter và Setter
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
