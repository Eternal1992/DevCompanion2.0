package com.ethereon.app.memory;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MemoryUtils {

    private static final String MEMORY_FOLDER = "nova_memory";
    private static final String MEMORY_FILE = "main_memory.mem";

    // Get the memory file reference
    public static File getMemoryFile(Context context) {
        File dir = new File(context.getFilesDir(), MEMORY_FOLDER);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, MEMORY_FILE);
    }

    // Save string to memory file (overwrites existing content)
    public static void saveToMemory(Context context, String data) {
        File memoryFile = getMemoryFile(context);
        try (FileWriter writer = new FileWriter(memoryFile, false)) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Append string to memory file
    public static void appendToMemory(Context context, String data) {
        File memoryFile = getMemoryFile(context);
        try (FileWriter writer = new FileWriter(memoryFile, true)) {
            writer.write("\n" + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load string from memory file
    public static String loadMemory(Context context) {
        File memoryFile = getMemoryFile(context);
        if (!memoryFile.exists()) return "";

        StringBuilder builder = new StringBuilder();
        try (Scanner scanner = new Scanner(memoryFile)) {
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString().trim();
    }
}