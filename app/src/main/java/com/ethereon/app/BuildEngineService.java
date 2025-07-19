package com.ethereon.app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Real background builder engine that turns prompts into structured app folders.
 * Nova calls this when the user finalizes a project idea.
 */
public class BuildEngineService {

    private static final String TAG = "BuildEngineService";

    public interface BuildCallback {
        void onProgressUpdate(String message, int percent);
        void onBuildComplete(File resultFile);
        void onBuildFailed(String error);
    }

    public static void buildAppFromPrompt(Context context, String idea, BuildCallback callback) {
        new Thread(() -> {
            try {
                sendProgress(callback, "Initializing build...", 5);

                AppBuilder builder = new AppBuilder(context);
                File projectFolder = builder.createNewProject(idea);

                sendProgress(callback, "Creating basic folder structure...", 20);
                File src = new File(projectFolder, "src/main/java/com/example/app");
                File res = new File(projectFolder, "src/main/res/layout");
                File manifest = new File(projectFolder, "src/main");

                src.mkdirs();
                res.mkdirs();
                manifest.mkdirs();

                sendProgress(callback, "Generating code from idea...", 40);

                // Simulated basic Java class
                String mainActivityCode = "package com.example.app;\n\n" +
                        "import android.app.Activity;\n" +
                        "import android.os.Bundle;\n\n" +
                        "public class MainActivity extends Activity {\n" +
                        "    @Override\n" +
                        "    protected void onCreate(Bundle savedInstanceState) {\n" +
                        "        super.onCreate(savedInstanceState);\n" +
                        "        setContentView(R.layout.activity_main);\n" +
                        "    }\n" +
                        "}\n";
                builder.writeToProject(src, "MainActivity.java", mainActivityCode);

                sendProgress(callback, "Generating layout XML...", 60);

                String layoutXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "    android:layout_width=\"match_parent\"\n" +
                        "    android:layout_height=\"match_parent\"\n" +
                        "    android:orientation=\"vertical\"\n" +
                        "    android:gravity=\"center\">\n\n" +
                        "    <TextView\n" +
                        "        android:layout_width=\"wrap_content\"\n" +
                        "        android:layout_height=\"wrap_content\"\n" +
                        "        android:text=\"Welcome to " + idea + "\" />\n\n" +
                        "</LinearLayout>";
                builder.writeToProject(res, "activity_main.xml", layoutXml);

                sendProgress(callback, "Writing manifest file...", 80);

                String manifestXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                        "    package=\"com.example.app\">\n\n" +
                        "    <application>\n" +
                        "        <activity android:name=\".MainActivity\">\n" +
                        "            <intent-filter>\n" +
                        "                <action android:name=\"android.intent.action.MAIN\" />\n" +
                        "                <category android:name=\"android.intent.category.LAUNCHER\" />\n" +
                        "            </intent-filter>\n" +
                        "        </activity>\n" +
                        "    </application>\n\n" +
                        "</manifest>";
                builder.writeToProject(manifest, "AndroidManifest.xml", manifestXml);

                sendProgress(callback, "Compressing project...", 95);
                builder.compressProject(projectFolder);

                sendComplete(callback, projectFolder);

            } catch (Exception e) {
                Log.e(TAG, "Build failed", e);
                sendFailed(callback, "Build error: " + e.getMessage());
            }
        }).start();
    }

    private static void sendProgress(BuildCallback callback, String msg, int percent) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onProgressUpdate(msg, percent));
    }

    private static void sendComplete(BuildCallback callback, File file) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onBuildComplete(file));
    }

    private static void sendFailed(BuildCallback callback, String error) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onBuildFailed(error));
    }
}