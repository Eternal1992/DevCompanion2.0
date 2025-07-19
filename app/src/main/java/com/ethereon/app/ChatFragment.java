package com.ethereon.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private EditText inputMessage;
    private Button sendButton;
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private final List<ChatMessage> chatMessages = new ArrayList<>();
    private ModelHandler modelHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        inputMessage = view.findViewById(R.id.inputMessage);
        sendButton = view.findViewById(R.id.sendButton);
        chatListView = view.findViewById(R.id.chatListView);

        chatAdapter = new ChatAdapter(requireContext(), chatMessages);
        chatListView.setAdapter(chatAdapter);
        modelHandler = new ModelHandler(requireContext());

        sendButton.setOnClickListener(v -> sendMessage());

        inputMessage.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                event.getAction() == KeyEvent.ACTION_DOWN)) {
                sendMessage();
                return true;
            }
            return false;
        });

        return view;
    }

    private void sendMessage() {
        String userMessage = inputMessage.getText().toString().trim();
        if (TextUtils.isEmpty(userMessage)) return;

        chatMessages.add(new ChatMessage(userMessage, ChatMessage.Type.USER));
        chatAdapter.notifyDataSetChanged();
        inputMessage.setText("");

        new InferenceTask().execute(userMessage);
    }

    private class InferenceTask extends AsyncTask<String, Void, String> {
        private String prompt;

        @Override
        protected String doInBackground(String... prompts) {
            prompt = prompts[0];
            return modelHandler.runModelInference(prompt);
        }

        @Override
        protected void onPostExecute(String response) {
            if (!TextUtils.isEmpty(response)) {
                chatMessages.add(new ChatMessage(response, ChatMessage.Type.AI));
                chatAdapter.notifyDataSetChanged();
            }
        }
    }
}