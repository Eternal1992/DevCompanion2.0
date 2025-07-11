package com.devcompanion;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_CHAT_MODEL_REQUEST_CODE = 100;
    private static final int PICK_CODE_MODEL_REQUEST_CODE = 101;

    private Uri chatModelUri;
    private Uri codeModelUri;

    private TextView txtChatModelPath;
    private TextView txtCodeModelPath;
    private TextView debugLog;
    private ScrollView logScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button selectChatBtn = findViewById(R.id.btnSelectChatModel);
        Button selectCodeBtn = findViewById(R.id.btnSelectCodeModel);
        Button confirmBtn = findViewById(R.id.btnConfirmModels);

        txtChatModelPath = findViewById(R.id.txtChatModelPath);
        txtCodeModelPath = findViewById(R.id.txtCodeModelPath);

        debugLog = findViewById(R.id.debug_log);
        logScroll = findViewById(R.id.log_scroll);

        selectChatBtn.setOnClickListener(view -> openModelSelector(PICK_CHAT_MODEL_REQUEST_CODE));
        selectCodeBtn.setOnClickListener(view -> openModelSelector(PICK_CODE_MODEL_REQUEST_CODE));

        confirmBtn.setOnClickListener(view -> {
            if (chatModelUri == null && codeModelUri == null) {
                toast("No models selected");
                return;
            }
            appendLog("Starting model copy...");
            new Thread(() -> {
                if (chatModelUri != null) copyModel(chatModelUri, "chat_model");
                if (codeModelUri != null) copyModel(codeModelUri, "code_model");
                appendLog("Model copy complete.");
            }).start();
        });
    }

    private void openModelSelector(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedUri = data.getData();
            if (selectedUri == null) {
                appendLog("No file selected");
                return;
            }

            if (requestCode == PICK_CHAT_MODEL_REQUEST_CODE) {
                chatModelUri = selectedUri;
                txtChatModelPath.setText(getFileName(selectedUri));
                appendLog("Selected chat model: " + getFileName(selectedUri));
            } else if (requestCode == PICK_CODE_MODEL_REQUEST_CODE) {
                codeModelUri = selectedUri;
                txtCodeModelPath.setText(getFileName(selectedUri));
                appendLog("Selected code model: " + getFileName(selectedUri));
            }
        }
    }

    private void copyModel(Uri uri, String destFileName) {
        File destDir = new File(getFilesDir(), "ai_models");
        if (!destDir.exists() && !destDir.mkdirs()) {
            appendLog("Failed to create model directory.");
            return;
        }

        File outFile = new File(destDir, destFileName);
        try (
            InputStream in = getContentResolver().openInputStream(uri);
            FileOutputStream out = new FileOutputStream(outFile)
        ) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            appendLog("Copied: " + destFileName);
        } catch (Exception e) {
            appendLog("Error copying " + destFileName + ": " + e.getMessage());
        }
    }

    private String getFileName(Uri uri) {
        try {
            String result = DocumentsContract.getDocumentId(uri);
            if (result != null && result.contains(":")) {
                return result.split(":")[1];
            }
        } catch (Exception e) {
            appendLog("Error retrieving filename");
        }
        return "Unknown";
    }

    private void toast(String msg) {
        runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }

    private void appendLog(String text) {
        runOnUiThread(() -> {
            debugLog.append(text + "\n");
            logScroll.post(() -> logScroll.fullScroll(View.FOCUS_DOWN));
        });
    }
}