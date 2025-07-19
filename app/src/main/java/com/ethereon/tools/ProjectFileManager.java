package com.ethereon.tools;

import android.content.Context;
import android.util.Log;

import com.ethereon.models.AppBlueprint;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ProjectFileManager {

    private static final String TAG = "ProjectFileManager";
    private final Context context;

    public ProjectFileManager(Context context) {
        this.context = context;
    }

    public File createProjectFolder(AppBlueprint blueprint) {
        File projectsRoot = new File(context.getFilesDir(), "generated_projects");
        if (!projectsRoot.exists()) projectsRoot.mkdirs();

        File appDir = new File(projectsRoot, blueprint.getAppName());
        if (!appDir.exists()) appDir.mkdirs();

        return appDir;
    }

    public void generateInitialFiles(AppBlueprint blueprint) {
        File root = createProjectFolder(blueprint);

        // Generate simple MainActivity.java
        File javaDir = new File(root, "java");
        javaDir.mkdirs();

        File mainJava = new File(javaDir, "MainActivity.java");
        try (FileWriter fw = new FileWriter(mainJava)) {
            fw.write("package " + blueprint.getPackageName() + ";\n\n");
            fw.write("import android.app.Activity;\n");
            fw.write("import android.os.Bundle;\n");
            fw.write("import android.widget.TextView;\n\n");
            fw.write("public class MainActivity extends Activity {\n");
            fw.write("    @Override\n");
            fw.write("    protected void onCreate(Bundle savedInstanceState) {\n");
            fw.write("        super.onCreate(savedInstanceState);\n");
            fw.write("        TextView tv = new TextView(this);\n");
            fw.write("        tv.setText(\"Welcome to " + blueprint.getAppName() + "!\");\n");
            fw.write("        setContentView(tv);\n");
            fw.write("    }\n");
            fw.write("}\n");
        } catch (IOException e) {
            Log.e(TAG, "Failed to write MainActivity.java: " + e.getMessage());
        }

        // Generate manifest
        File manifestFile = new File(root, "AndroidManifest.xml");
        try (FileWriter fw = new FileWriter(manifestFile)) {
            fw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            fw.write("<manifest package=\"" + blueprint.getPackageName() + "\" xmlns:android=\"http://schemas.android.com/apk/res/android\">\n");
            fw.write("    <application android:label=\"" + blueprint.getAppName() + "\">\n");
            fw.write("        <activity android:name=\".MainActivity\">\n");
            fw.write("            <intent-filter>\n");
            fw.write("                <action android:name=\"android.intent.action.MAIN\" />\n");
            fw.write("                <category android:name=\"android.intent.category.LAUNCHER\" />\n");
            fw.write("            </intent-filter>\n");
            fw.write("        </activity>\n");
            fw.write("    </application>\n");
            fw.write("</manifest>\n");
        } catch (IOException e) {
            Log.e(TAG, "Failed to write AndroidManifest.xml: " + e.getMessage());
        }
    }
}