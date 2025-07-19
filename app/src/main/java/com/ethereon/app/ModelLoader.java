package com.ethereon.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ModelLoader {

    private static final int REQUEST_CODE_PICK_MODEL = 101;
    private static final String TAG = "ModelLoader";

    private final Activity activity;
    private final ModelHandler modelHandler;

    public ModelLoader(Activity activity, ModelHandler modelHandler) {
        this.activity = activity;
        this.modelHandler = modelHandler;
    }

    public void launchModelPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, REQUEST_CODE_PICK_MODEL);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_MODEL && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                File modelFile = copyUriToInternalStorage(uri);
                if (modelFile != null) {
                    modelHandler.setModelFile(modelFile);
                    Log.d(TAG, "Model file set: " + modelFile.getAbsolutePath());
                }
            }
        }
    }

    private File copyUriToInternalStorage(Uri uri) {
        try {
            String fileName = getFileNameFromUri(uri);
            File destFile = new File(activity.getFilesDir(), fileName);

            try (InputStream in = activity.getContentResolver().openInputStream(uri);
                 FileOutputStream out = new FileOutputStream(destFile)) {

                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }

            Log.d(TAG, "Model copied to internal storage: " + destFile.getAbsolutePath());
            return destFile;

        } catch (Exception e) {
            Log.e(TAG, "Error copying model", e);
            return null;
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = "model.bin";
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    result = cursor.getString(nameIndex);
                }
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    public static int getRequestCode() {
        return REQUEST_CODE_PICK_MODEL;
    }
}