package com.ethereon.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModelChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private EditText inputMessage;
    private ImageButton btnSend, btnVoice;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    private ModelHandler modelHandler;
    private VoiceInputHandler voiceInputHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);

        // Initialize views
        recyclerChat = findViewById(R.id.recyclerChat);
        inputMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);
        btnVoice = findViewById(R.id.btnVoice);

        // Init message list and adapter
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(chatAdapter);

        // Setup model handler
        modelHandler = new ModelHandler(this);

        // If model file was passed in
        File passedModel = (File) getIntent().getSerializableExtra("modelFile");
        if (passedModel != null) {
            modelHandler.setModelFile(passedModel);
        }

        // Setup voice input handler
        voiceInputHandler = new VoiceInputHandler(this);
        voiceInputHandler.setVoiceInputListener(result -> {
            inputMessage.setText(result);
            processUserMessage(result);
        });

        // Send button logic
        btnSend.setOnClickListener(v -> {
            String message = inputMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                processUserMessage(message);
                inputMessage.setText("");
            }
        });

        // Voice button logic
        btnVoice.setOnClickListener(v -> voiceInputHandler.startListening());
    }

    private void processUserMessage(String message) {
        // Add user's message to the chat
        ChatMessage userMessage = new ChatMessage(message, ChatMessage.Sender.USER);
        chatAdapter.addMessage(userMessage);
        recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);

        // Get response from the model
        String aiResponse = modelHandler.runModelInference(message);

        // Add AI's response to the chat
        ChatMessage aiMessage = new ChatMessage(aiResponse, ChatMessage.Sender.AI);
        chatAdapter.addMessage(aiMessage);
        recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        voiceInputHandler.handleActivityResult(requestCode, resultCode, data);
    }
}