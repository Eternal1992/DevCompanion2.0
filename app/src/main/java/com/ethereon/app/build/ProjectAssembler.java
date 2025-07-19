package com.ethereon.app.build;

import android.content.Context;
import android.util.Log;

import com.ethereon.app.models.AppIdeaModel;
import com.ethereon.app.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ProjectAssembler {

    private static final String TAG = "ProjectAssembler";

    public static File assembleProject(Context context, AppIdeaModel appIdea) throws IOException {
        File projectDir = new File(Utils.getAppStorageDirectory(context), appIdea.getAppName());
        if (!projectDir.exists() && !projectDir.mkdirs()) {
            throw new IOException("Failed to create project directory");
        }

        // Create basic structure
        File srcDir = new File(projectDir, "src/main/java/com/example/app");
        File resDir = new File(projectDir, "src/main/res/layout");
        File manifestFile = new File(projectDir, "src/main/AndroidManifest.xml");

        srcDir.mkdirs();
        resDir.mkdirs();

        // Generate MainActivity.java
        File mainActivity = new File(srcDir, "MainActivity.java");
        try (FileWriter writer = new FileWriter(mainActivity)) {
            writer.write(generateMainActivityCode(appIdea.getAppName()));
        }

        // Generate layout XML
        File layoutFile = new File(resDir, "activity_main.xml");
        try (FileWriter writer = new FileWriter(layoutFile)) {
            writer.write(generateLayoutXml());
        }

        // Generate AndroidManifest.xml
        try (FileWriter writer = new FileWriter(manifestFile)) {
            writer.write(generateManifestXml(appIdea.getAppName()));
        }

        Log.i(TAG, "Project assembled at: " + projectDir.getAbsolutePath());
        return projectDir;
    }

    private static String generateMainActivityCode(String appName) {
        return "package com.example.app;\n\n" +
                "import android.os.Bundle;\n" +
                "import androidx.appcompat.app.AppCompatActivity;\n\n" +
                "public class MainActivity extends AppCompatActivity {\n" +
                "    @Override\n" +
                "    protected void onCreate(Bundle savedInstanceState) {\n" +
                "        super.onCreate(savedInstanceState);\n" +
                "        setContentView(R.layout.activity_main);\n" +
                "    }\n" +
                "}\n";
    }

    private static String generateLayoutXml() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    android:layout_width=\"match_parent\"\n" +
                "    android:layout_height=\"match_parent\"\n" +
                "    android:orientation=\"vertical\"\n" +
                "    android:gravity=\"center\"\n" +
                "    android:padding=\"16dp\">\n\n" +
                "    <TextView\n" +
                "        android:layout_width=\"wrap_content\"\n" +
                "        android:layout_height=\"wrap_content\"\n" +
                "        android:text=\"Hello from " + "MainActivity" + "!\"\n" +
                "        android:textSize=\"24sp\" />\n\n" +
                "</LinearLayout>";
    }

    private static String generateManifestXml(String appName) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    package=\"com.example.app\">\n\n" +
                "    <application\n" +
                "        android:label=\"" + appName + "\"\n" +
                "        android:icon=\"@mipmap/ic_launcher\">\n" +
                "        <activity android:name=\".MainActivity\">\n" +
                "            <intent-filter>\n" +
                "                <action android:name=\"android.intent.action.MAIN\" />\n" +
                "                <category android:name=\"android.intent.category.LAUNCHER\" />\n" +
                "            </intent-filter>\n" +
                "        </activity>\n" +
                "    </application>\n" +
                "</manifest>";
    }
}