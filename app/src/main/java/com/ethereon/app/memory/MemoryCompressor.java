package com.ethereon.app.memory;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class MemoryCompressor {

    private static final String TAG = "MemoryCompressor";

    // Compress a string using GZIP and Base64 encode the result
    public static String compress(String input) {
        if (input == null || input.isEmpty()) return input;

        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream);
            gzipStream.write(input.getBytes());
            gzipStream.close();

            byte[] compressedBytes = byteStream.toByteArray();
            return Base64.encodeToString(compressedBytes, Base64.DEFAULT);
        } catch (IOException e) {
            Log.e(TAG, "Compression failed", e);
            return input;
        }
    }

    // Decompress a Base64-encoded GZIP string back into original string
    public static String decompress(String input) {
        if (input == null || input.isEmpty()) return input;

        try {
            byte[] compressedBytes = Base64.decode(input, Base64.DEFAULT);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedBytes);
            GZIPInputStream gzipStream = new GZIPInputStream(byteStream);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            while ((len = gzipStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            return outputStream.toString();
        } catch (IOException e) {
            Log.e(TAG, "Decompression failed", e);
            return input;
        }
    }
}