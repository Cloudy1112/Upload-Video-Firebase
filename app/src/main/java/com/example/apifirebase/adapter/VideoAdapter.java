package com.example.apifirebase.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apifirebase.ProfileActivity;
import com.example.apifirebase.R;
import com.example.apifirebase.model.VideoModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<VideoModel> videoList;
    private Context context;

    public VideoAdapter(List<VideoModel> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_container, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModel video = videoList.get(position);

        // 1. Set thông tin video
        holder.tvTitle.setText(video.getTitle());
        holder.tvDesc.setText("Mô tả video..."); // Bạn có thể thêm trường desc vào Model sau
        holder.tvEmail.setText(video.getUploaderId()); // Tạm thời hiện ID, muốn hiện email phải query thêm bảng Users
        holder.tvLikeCount.setText(String.valueOf(video.getLikeCount()));

        // 2. Load Video
        holder.videoView.setVideoPath(video.getVideoUrl());

        // Sự kiện khi video chuẩn bị xong -> Chạy và ẩn loading
        holder.videoView.setOnPreparedListener(mp -> {
            holder.progressBar.setVisibility(View.GONE);
            mp.start();

            // Xử lý video tỷ lệ khung hình
            float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
            float screenRatio = holder.videoView.getWidth() / (float) holder.videoView.getHeight();
            float scale = videoRatio / screenRatio;
            if (scale >= 1f) {
                holder.videoView.setScaleX(scale);
            } else {
                holder.videoView.setScaleY(1f / scale);
            }
        });

        holder.videoView.setOnCompletionListener(mp -> mp.start()); // Lặp lại video

        // 3. Load Avatar User hiện tại (Góc trên phải)
        // Yêu cầu: Click vào mở ProfileActivity
        holder.imgCurrentUser.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            context.startActivity(intent);
        });

        // TODO: Load ảnh avatar thực tế từ Firebase (cần code lấy thông tin user)
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        TextView tvTitle, tvDesc, tvEmail, tvLikeCount;
        ProgressBar progressBar;
        CircleImageView imgCurrentUser, imgUploader;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            tvTitle = itemView.findViewById(R.id.tvVideoTitle);
            tvDesc = itemView.findViewById(R.id.tvVideoDesc);
            tvEmail = itemView.findViewById(R.id.tvUploaderEmail);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            progressBar = itemView.findViewById(R.id.progressBar);
            imgCurrentUser = itemView.findViewById(R.id.imgCurrentUserAvatar);
            imgUploader = itemView.findViewById(R.id.imgUploaderAvatar);
        }
    }
}
