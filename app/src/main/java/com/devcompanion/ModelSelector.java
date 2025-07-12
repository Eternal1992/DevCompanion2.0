package com.devcompanion;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class ModelSelector extends Fragment {

    private static final int PICK_CHAT_MODEL_FILE = 201;
    private static final int PICK_CODE_MODEL_FILE = 202;

    private MaterialButton pickChatButton;
    private MaterialButton pickCodeButton;
    private MaterialButton confirmButton;
    private MaterialButton resetChatButton;
    private MaterialButton resetCodeButton;
    private ProgressBar progressSpinner;
    private TextView debugStatus;
    private TextView chatModelDisplay;
    private TextView codeModelDisplay;

    private SharedPreferences prefs;

    public ModelSelector() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_model_selector, container, false);

        prefs = requireActivity().getSharedPreferences("devcompanion_models", Activity.MODE_PRIVATE);

        // Initialize views from layout
        chatModelDisplay = view.findViewById(R.id.chat_model_display);
        codeModelDisplay = view.findViewById(R.id.code_model_display);
        pickChatButton = view.findViewById(R.id.button_pick_chat_model);
        pickCodeButton = view.findViewById(R.id.button_pick_code_model);
        confirmButton = view.findViewById(R.id.button_confirm_models);
        resetChatButton = view.findViewById(R.id.button_reset_chat_model);
        resetCodeButton = view.findViewById(R.id.button_reset_code_model);
        progressSpinner = view.findViewById(R.id.progress_spinner);
        debugStatus = view.findViewById(R.id.debug_status);

        // Load saved filenames or set defaults
        String savedChatModel = prefs.getString("chat_model", "");
        if (!savedChatModel.isEmpty()) {
            chatModelDisplay.setText(savedChatModel);
            chatModelDisplay.setTextColor(getResources().getColor(R.color.text_primary));
        } else {
            chatModelDisplay.setText("No Chat Model Selected");
            chatModelDisplay.setTextColor(getResources().getColor(R.color.text_secondary));
        }

        String savedCodeModel = prefs.getString("code_model", "");
        if (!savedCodeModel.isEmpty()) {
            codeModelDisplay.setText(savedCodeModel);
            codeModelDisplay.setTextColor(getResources().getColor(R.color.text_primary));
        } else {
            codeModelDisplay.setText("No Code Model Selected");
            codeModelDisplay.setTextColor(getResources().getColor(R.color.text_secondary));
        }

        // Button listeners
        pickChatButton.setOnClickListener(v -> launchPicker(PICK_CHAT_MODEL_FILE));
        pickCodeButton.setOnClickListener(v -> launchPicker(PICK_CODE_MODEL_FILE));

        resetChatButton.setOnClickListener(v -> {
            chatModelDisplay.setText("No Chat Model Selected");
            chatModelDisplay.setTextColor(getResources().getColor(R.color.text_secondary));
            prefs.edit().remove("chat_model").apply();
            debugStatus.setText("Status: Chat model reset.");
        });

        resetCodeButton.setOnClickListener(v -> {
            codeModelDisplay.setText("No Code Model Selected");
            codeModelDisplay.setTextColor(getResources().getColor(R.color.text_secondary));
            prefs.edit().remove("code_model").apply();
            debugStatus.setText("Status: Code model reset.");
        });

        confirmButton.setOnClickListener(v -> {
            String chat = chatModelDisplay.getText().toString();
            String code = codeModelDisplay.getText().toString();

            if (chat.equals("No Chat Model Selected") || chat.isEmpty()
                    || code.equals("No Code Model Selected") || code.isEmpty()) {
                Toast.makeText(getContext(), "Please select both models.", Toast.LENGTH_SHORT).show();
                return;
            }

            progressSpinner.setVisibility(View.VISIBLE);
            debugStatus.setText("Status: Saving...");

            prefs.edit()
                    .putString("chat_model", chat)
                    .putString("code_model", code)
                    .apply();

            requireView().postDelayed(() -> {
                progressSpinner.setVisibility(View.GONE);
                debugStatus.setText("Status: Models saved.");
            }, 1000);
        });

        return view;
    }

    private void launchPicker(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Model File"), requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String fileName = getFileName(uri);
                if (requestCode == PICK_CHAT_MODEL_FILE) {
                    chatModelDisplay.setText(fileName);
                    chatModelDisplay.setTextColor(getResources().getColor(R.color.text_primary));
                    prefs.edit().putString("chat_model", fileName).apply();
                } else if (requestCode == PICK_CODE_MODEL_FILE) {
                    codeModelDisplay.setText(fileName);
                    codeModelDisplay.setTextColor(getResources().getColor(R.color.text_primary));
                    prefs.edit().putString("code_model", fileName).apply();
                }
                debugStatus.setText("Status: Selected file: " + fileName);
            }
        }
    }

    private String getFileName(Uri uri) {
        String name = "unknown";
        try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    name = cursor.getString(index);
                }
            }
        } catch (Exception ignored) {
        }
        return name;
    }
}