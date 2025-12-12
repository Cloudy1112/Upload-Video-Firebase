package com.example.apifirebase.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apifirebase.R;
import com.example.apifirebase.model.MessageModel;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<MessageModel> messageList = new ArrayList<>();
    private String myUserId; // ID của người dùng hiện tại

    public ChatAdapter(String myUserId) {
        this.myUserId = myUserId;
    }

    // Hàm thêm tin nhắn mới vào list
    public void addMessage(MessageModel message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        MessageModel message = messageList.get(position);

        // Logic kiểm tra: Nếu senderId trùng với myUserId -> Hiện bên phải, ngược lại bên trái
        if (message.getSenderid().equals(myUserId)) {
            holder.tvRight.setText(message.getContent());
            holder.tvRight.setVisibility(View.VISIBLE);
            holder.tvLeft.setVisibility(View.GONE);
        } else {
            holder.tvLeft.setText(message.getContent());
            holder.tvLeft.setVisibility(View.VISIBLE);
            holder.tvRight.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvLeft, tvRight;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLeft = itemView.findViewById(R.id.tvMessageLeft);
            tvRight = itemView.findViewById(R.id.tvMessageRight);
        }
    }
}
