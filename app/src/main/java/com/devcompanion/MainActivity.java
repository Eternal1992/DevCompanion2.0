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

    private static final int PICK_MODELS_REQUEST_CODE = 100;
    private Uri[] selectedUris;
    private TextView debugLog;
    private ScrollView logScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button chooseBtn = findViewById(R.id.choose_models_btn);
        Button confirmBtn = findViewById(R.id.confirm_models_btn);
        debugLog = findViewById(R.id.debug_log);
        logScroll = findViewById(R.id.log_scroll);

        chooseBtn.setOnClickListener(view -> openModelSelector());

        confirmBtn.setOnClickListener(view -> {
            if (selectedUris == null || selectedUris.length == 0) {
                toast("No models selected");
            } else {
                appendLog("🔄 Starting model copy...");
                new Thread(() -> copyModels(selectedUris)).start();
            }
        });
    }

    private void openModelSelector() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_MODELS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MODELS_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                selectedUris = new Uri[count];
                for (int i = 0; i < count; i++) {
                    selectedUris[i] = data.getClipData().getItemAt(i).getUri();
                }
            } else if (data.getData() != null) {
                selectedUris = new Uri[]{data.getData()};
            }
            appendLog("📂 Selected " + selectedUris.length + " model(s)");
        }
    }

    private void copyModels(Uri[] uris) {
        File destDir = new File(getFilesDir(), "ai_models");
        if (!destDir.exists() && !destDir.mkdirs()) {
            appendLog("❌ Failed to create model directory.");
            return;
        }

        for (Uri uri : uris) {
            try {
                String fileName = getFileName(uri);
                if (fileName == null) {
                    appendLog("⚠️ Skipped unnamed file");
                    continue;
                }

                File outFile = new File(destDir, fileName);
                if (outFile.exists()) {
                    appendLog("↪️ Replacing existing model: " + fileName);
                    outFile.delete();
                } else {
                    appendLog("📄 Copying model: " + fileName);
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
                    appendLog("✅ Copied: " + fileName);
                }

            } catch (Exception e) {
                appendLog("❌ Error copying: " + e.getMessage());
            }
        }

        appendLog("✅ Model copy complete.");
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