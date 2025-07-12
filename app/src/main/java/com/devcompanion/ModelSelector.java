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
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class ModelSelector extends Fragment {

    private static final int PICK_MODEL_FILE_REQUEST = 101;

    private MaterialAutoCompleteTextView chatModelDropdown;
    private MaterialAutoCompleteTextView codeModelDropdown;
    private MaterialButton confirmButton;
    private MaterialButton filePickerButton;
    private MaterialButton resetChatButton;
    private MaterialButton resetCodeButton;
    private ProgressBar progressSpinner;
    private TextView debugStatus;

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

        // Bind views
        chatModelDropdown = view.findViewById(R.id.chat_model_spinner);
        codeModelDropdown = view.findViewById(R.id.code_model_spinner);
        confirmButton = view.findViewById(R.id.button_confirm_models);
        progressSpinner = view.findViewById(R.id.progress_spinner);
        debugStatus = view.findViewById(R.id.debug_status);

        // Dropdown model options
        String[] chatModels = {"gpt-4", "gpt-3.5", "claude-3"};
        String[] codeModels = {"codex", "codegen", "starcoder"};

        chatModelDropdown.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, chatModels));
        codeModelDropdown.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, codeModels));

        // Load saved model names
        prefs = requireActivity().getSharedPreferences("devcompanion_models", Activity.MODE_PRIVATE);
        chatModelDropdown.setText(prefs.getString("chat_model", ""), false);
        codeModelDropdown.setText(prefs.getString("code_model", ""), false);

        // Confirm models
        confirmButton.setOnClickListener(v -> {
            String selectedChat = chatModelDropdown.getText().toString();
            String selectedCode = codeModelDropdown.getText().toString();

            if (selectedChat.isEmpty() || selectedCode.isEmpty()) {
                Toast.makeText(getContext(), "Please select both models.", Toast.LENGTH_SHORT).show();
                return;
            }

            debugStatus.setText("Status: Saving...");
            progressSpinner.setVisibility(View.VISIBLE);

            prefs.edit()
                    .putString("chat_model", selectedChat)
                    .putString("code_model", selectedCode)
                    .apply();

            requireView().postDelayed(() -> {
                debugStatus.setText("Status: Models saved.");
                progressSpinner.setVisibility(View.GONE);
            }, 1000);
        });

        // Add file picker button (already added dynamically)
        filePickerButton = new MaterialButton(requireContext());
        filePickerButton.setText("Pick Local Model File");
        filePickerButton.setBackgroundTintList(requireContext().getColorStateList(R.color.accent));
        filePickerButton.setTextColor(requireContext().getColor(R.color.button_text));
        filePickerButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Model File"), PICK_MODEL_FILE_REQUEST);
        });

        // Reset buttons for chat and code models
        resetChatButton = new MaterialButton(requireContext());
        resetChatButton.setText("Reset Chat Model");
        resetChatButton.setBackgroundTintList(requireContext().getColorStateList(R.color.accent));
        resetChatButton.setTextColor(requireContext().getColor(R.color.button_text));
        resetChatButton.setOnClickListener(v -> {
            chatModelDropdown.setText("", false);
            prefs.edit().remove("chat_model").apply();
            Toast.makeText(getContext(), "Chat model reset.", Toast.LENGTH_SHORT).show();
        });

        resetCodeButton = new MaterialButton(requireContext());
        resetCodeButton.setText("Reset Code Model");
        resetCodeButton.setBackgroundTintList(requireContext().getColorStateList(R.color.accent));
        resetCodeButton.setTextColor(requireContext().getColor(R.color.button_text));
        resetCodeButton.setOnClickListener(v -> {
            codeModelDropdown.setText("", false);
            prefs.edit().remove("code_model").apply();
            Toast.makeText(getContext(), "Code model reset.", Toast.LENGTH_SHORT).show();
        });

        // Add buttons to layout
        ViewGroup rootLayout = view.findViewById(R.id.model_selector_root);
        rootLayout.addView(filePickerButton);
        rootLayout.addView(resetChatButton);
        rootLayout.addView(resetCodeButton);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_MODEL_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String fileName = getFileName(uri);
                Toast.makeText(getContext(), "Selected file: " + fileName, Toast.LENGTH_SHORT).show();
                debugStatus.setText("Status: Selected file: " + fileName);

                // Update chat model dropdown text and prefs immediately on file select
                chatModelDropdown.setText(fileName, false);
                prefs.edit().putString("chat_model", fileName).apply();
            }
        }
    }

    private String getFileName(Uri uri) {
        String name = "unknown";
        try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } catch (Exception e) {
            // Optional: log or handle error here
        }
        return name;
    }
}