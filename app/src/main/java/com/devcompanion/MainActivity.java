package com.devcompanion;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int REQUEST_CHAT_MODEL = 100;
    private static final int REQUEST_CODE_MODEL = 101;

    private Uri chatModelUri = null;
    private Uri codeModelUri = null;

    private TextView chatModelPath;
    private TextView codeModelPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button selectChatBtn = findViewById(R.id.btnSelectChatModel);
        Button selectCodeBtn = findViewById(R.id.btnSelectCodeModel);
        Button confirmBtn = findViewById(R.id.btnConfirmModels);

        chatModelPath = findViewById(R.id.txtChatModelPath);
        codeModelPath = findViewById(R.id.txtCodeModelPath);

        selectChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickModelFile(REQUEST_CHAT_MODEL);
            }
        });

        selectCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickModelFile(REQUEST_CODE_MODEL);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatModelUri != null && codeModelUri != null) {
                    Toast.makeText(MainActivity.this, "Models selected successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please select both models.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pickModelFile(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select AI Model"), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String fileName = getFileName(uri);
            if (requestCode == REQUEST_CHAT_MODEL) {
                chatModelUri = uri;
                chatModelPath.setText("Chat AI: " + fileName);
            } else if (requestCode == REQUEST_CODE_MODEL) {
                codeModelUri = uri;
                codeModelPath.setText("Code AI: " + fileName);
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = "Unknown";
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        return result;
    }
}