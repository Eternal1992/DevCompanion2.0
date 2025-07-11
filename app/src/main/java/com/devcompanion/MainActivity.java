package com.devcompanion;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

    private static final int PICK_CHAT_MODEL_CODE = 1;
    private static final int PICK_CODE_MODEL_CODE = 2;

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

        Button btnChatModel = findViewById(R.id.btnSelectChatModel);
        Button btnCodeModel = findViewById(R.id.btnSelectCodeModel);
        Button btnConfirm = findViewById(R.id.btnConfirmModels);

        txtChatModelPath = findViewById(R.id.txtChatModelPath);
        txtCodeModelPath = findViewById(R.id.txtCodeModelPath);
        debugLog = findViewById(R.id.debug_log);
        logScroll = findViewById(R.id.log_scroll);

        btnChatModel.setOnClickListener(v -> openModelSelector(PICK_CHAT_MODEL_CODE));
        btnCodeModel.setOnClickListener(v -> openModelSelector(PICK_CODE_MODEL_CODE));
        btnConfirm.setOnClickListener(v -> {
            if (chatModelUri == null || codeModelUri == null) {
                toast("Please select both models first.");
            } else {
                appendLog("🔄 Starting model copy...");
                new Thread(() -> copyModels()).start();
            }
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
            Uri uri = data.getData();
            if (uri != null) {
                String fileName = getFileName(uri);
                if (requestCode == PICK_CHAT_MODEL_CODE) {
                    chatModelUri = uri;
                    txtChatModelPath.setText(fileName != null ? fileName : uri.toString());
                    appendLog("📥 Chat model selected: " + fileName);
                } else if (requestCode == PICK_CODE_MODEL_CODE) {
                    codeModelUri = uri;
                    txtCodeModelPath.setText(fileName != null ? fileName : uri.toString());
                    appendLog("📥 Code model selected: " + fileName);
                }
            }
        }
    }

    private void copyModels() {
        File destDir = new File(getFilesDir(), "ai_models");
        if (!destDir.exists() && !destDir.mkdirs()) {
            appendLog("❌ Failed to create model directory.");
            return;
        }

        copySingleModel(chatModelUri, destDir, "chat model");
        copySingleModel(codeModelUri, destDir, "code model");

        appendLog("✅ Model copy complete.");
    }

    private void copySingleModel(Uri uri, File destDir, String label) {
        try {
            String fileName = getFileName(uri);
            if (fileName == null) {
                appendLog("⚠️ Skipped unnamed " + label);
                return;
            }

            File outFile = new File(destDir, fileName);
            if (outFile.exists()) {
                appendLog("↪️ Replacing existing file: " + fileName);
                outFile.delete();
            }

            try (
                InputStream in = getContentResolver().openInputStream(uri);
                FileOutputStream out = new FileOutputStream(outFile)
            ) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                appendLog("✅ Copied " + label + ": " + fileName);
            }

        } catch (Exception e) {
            appendLog("❌ Error copying " + label + ": " + e.getMessage());
        }
    }

    private String getFileName(Uri uri) {
        try {
            String result = DocumentsContract.getDocumentId(uri);
            if (result != null && result.contains(":")) {
                return result.split(":")[1];
            }
        } catch (Exception e) {
            appendLog("⚠️ Error retrieving filename");
        }
        return null;
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