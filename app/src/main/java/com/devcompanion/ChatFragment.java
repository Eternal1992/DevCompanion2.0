package com.devcompanion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatFragment extends Fragment {

    private static final int SPEECH_REQUEST_CODE = 100;

    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ImageButton sendButton;
    private ImageButton micButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = view.findViewById(R.id.chat_recycler_view);
        inputMessage = view.findViewById(R.id.input_message);
        sendButton = view.findViewById(R.id.button_send);
        micButton = view.findViewById(R.id.button_speech); // ← fixed ID here

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> sendMessage());

        micButton.setOnClickListener(v -> startVoiceInput());

        inputMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        return view;
    }

    private void sendMessage() {
        String message = inputMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) return;

        ChatMessage userMessage = new ChatMessage(message, true);
        messageList.add(userMessage);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
        inputMessage.setText("");

        recyclerView.postDelayed(() -> {
            ChatMessage botMessage = new ChatMessage("You said: " + message, false);
            messageList.add(botMessage);
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);
        }, 1000);
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally show a toast here
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                inputMessage.setText(results.get(0));
                inputMessage.setSelection(inputMessage.getText().length());
            }
        }
    }
}