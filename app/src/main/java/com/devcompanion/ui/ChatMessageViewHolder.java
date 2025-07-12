package com.devcompanion.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devcompanion.R;
import com.devcompanion.ChatMessage;

public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    public TextView messageTextView;

    public ChatMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.chat_message_text);
    }

    public void bind(ChatMessage chatMessage) {
        messageTextView.setText(chatMessage.getMessage());
        // You can also add logic here to style user vs AI messages differently
    }
}