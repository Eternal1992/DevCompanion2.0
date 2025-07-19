package com.ethereon.app.build;

import android.content.Context;
import android.util.Log;

import com.ethereon.app.models.AppIdeaModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ProjectScaffolder {

    private static final String TAG = "ProjectScaffolder";
    private static final String PROJECT_ROOT = "projects";

    private final Context context;

    public ProjectScaffolder(Context context) {
        this.context = context;
    }

    public File createProjectStructure(AppIdeaModel appIdea) {
        String safeName = appIdea.getAppName().replaceAll("[^a-zA-Z0-9_]", "_");
        File rootDir = new File(context.getFilesDir(), PROJECT_ROOT);
        if (!rootDir.exists()) rootDir.mkdirs();

        File projectDir = new File(rootDir, safeName);
        if (!projectDir.exists()) projectDir.mkdirs();

        File srcDir = new File(projectDir, "src");
        File resDir = new File(projectDir, "res");
        File assetsDir = new File(projectDir, "assets");
        File libsDir = new File(projectDir, "libs");
        File buildDir = new File(projectDir, "build");
        File manifestFile = new File(projectDir, "AndroidManifest.xml");
        File gradleFile = new File(projectDir, "build.gradle");

        srcDir.mkdirs();
        resDir.mkdirs();
        assetsDir.mkdirs();
        libsDir.mkdirs();
        buildDir.mkdirs();

        writeManifest(manifestFile, appIdea.getAppName());
        writeGradleFile(gradleFile, appIdea.getAppName());

        Log.i(TAG, "Scaffolded project: " + projectDir.getAbsolutePath());
        return projectDir;
    }

    private void writeManifest(File manifest, String appName) {
        String packageName = "com.generated." + appName.toLowerCase().replaceAll("\\s+", "");
        String content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
                "    package=\"" + packageName + "\">\n" +
                "    <application\n" +
                "        android:allowBackup=\"true\"\n" +
                "        android:label=\"" + appName + "\"\n" +
                "        android:theme=\"@style/Theme.AppCompat.Light.DarkActionBar\">\n" +
                "        <activity android:name=\".MainActivity\">\n" +
                "            <intent-filter>\n" +
                "                <action android:name=\"android.intent.action.MAIN\" />\n" +
                "                <category android:name=\"android.intent.category.LAUNCHER\" />\n" +
                "            </intent-filter>\n" +
                "        </activity>\n" +
                "    </application>\n" +
                "</manifest>";

        try (FileWriter fw = new FileWriter(manifest)) {
            fw.write(content);
        } catch (IOException e) {
            Log.e(TAG, "Error writing manifest", e);
        }
    }

    private void writeGradleFile(File gradle, String appName) {
        String content = "apply plugin: 'com.android.application'\n\n" +
                "android {\n" +
                "    compileSdkVersion 33\n" +
                "    defaultConfig {\n" +
                "        applicationId \"com.generated." + appName.toLowerCase().replaceAll("\\s+", "") + "\"\n" +
                "        minSdkVersion 24\n" +
                "        targetSdkVersion 33\n" +
                "        versionCode 1\n" +
                "        versionName \"1.0\"\n" +
                "    }\n" +
                "    buildTypes {\n" +
                "        release {\n" +
                "            minifyEnabled false\n" +
                "            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'\n" +
                "        }\n" +
                "    }\n" +
                "}\n\n" +
                "dependencies {\n" +
                "    implementation 'androidx.appcompat:appcompat:1.6.1'\n" +
                "    implementation 'com.google.android.material:material:1.9.0'\n" +
                "}";

        try (FileWriter fw = new FileWriter(gradle)) {
            fw.write(content);
        } catch (IOException e) {
            Log.e(TAG, "Error writing build.gradle", e);
        }
    }
}