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

        chatModelDisplay = new TextView(requireContext());
        chatModelDisplay.setTextColor(getResources().getColor(R.color.text_primary));
        codeModelDisplay = new TextView(requireContext());
        codeModelDisplay.setTextColor(getResources().getColor(R.color.text_primary));

        pickChatButton = new MaterialButton(requireContext());
        pickChatButton.setText("Pick Chat Model File");
        pickChatButton.setBackgroundTintList(requireContext().getColorStateList(R.color.accent));
        pickChatButton.setTextColor(requireContext().getColor(R.color.button_text));
        pickChatButton.setOnClickListener(v -> launchPicker(PICK_CHAT_MODEL_FILE));

        pickCodeButton = new MaterialButton(requireContext());
        pickCodeButton.setText("Pick Code Model File");
        pickCodeButton.setBackgroundTintList(requireContext().getColorStateList(R.color.accent));
        pickCodeButton.setTextColor(requireContext().getColor(R.color.button_text));
        pickCodeButton.setOnClickListener(v -> launchPicker(PICK_CODE_MODEL_FILE));

        confirmButton = view.findViewById(R.id.button_confirm_models);
        progressSpinner = view.findViewById(R.id.progress_spinner);
        debugStatus = view.findViewById(R.id.debug_status);

        confirmButton.setOnClickListener(v -> {
            String chat = chatModelDisplay.getText().toString();
            String code = codeModelDisplay.getText().toString();

            if (chat.isEmpty() || code.isEmpty()) {
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

        resetChatButton = new MaterialButton(requireContext());
        resetChatButton.setText("Reset Chat Model");
        resetChatButton.setBackgroundTintList(requireContext().getColorStateList(R.color.accent));
        resetChatButton.setTextColor(requireContext().getColor(R.color.button_text));
        resetChatButton.setOnClickListener(v -> {
            chatModelDisplay.setText("");
            prefs.edit().remove("chat_model").apply();
        });

        resetCodeButton = new MaterialButton(requireContext());
        resetCodeButton.setText("Reset Code Model");
        resetCodeButton.setBackgroundTintList(requireContext().getColorStateList(R.color.accent));
        resetCodeButton.setTextColor(requireContext().getColor(R.color.button_text));
        resetCodeButton.setOnClickListener(v -> {
            codeModelDisplay.setText("");
            prefs.edit().remove("code_model").apply();
        });

        chatModelDisplay.setText(prefs.getString("chat_model", ""));
        codeModelDisplay.setText(prefs.getString("code_model", ""));

        ViewGroup rootLayout = view.findViewById(R.id.model_selector_root);
        rootLayout.addView(chatModelDisplay);
        rootLayout.addView(pickChatButton);
        rootLayout.addView(codeModelDisplay);
        rootLayout.addView(pickCodeButton);
        rootLayout.addView(resetChatButton);
        rootLayout.addView(resetCodeButton);

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
                    prefs.edit().putString("chat_model", fileName).apply();
                } else if (requestCode == PICK_CODE_MODEL_FILE) {
                    codeModelDisplay.setText(fileName);
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
                name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } catch (Exception ignored) {}
        return name;
    }
}