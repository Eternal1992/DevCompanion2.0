package com.ethereon.app.build;

import android.content.Context;
import android.util.Log;

import com.ethereon.app.models.AppIdeaModel;

import java.io.File;
import java.io.IOException;

public class FinalApkBuilder {

    private static final String TAG = "FinalApkBuilder";
    private final Context context;

    public FinalApkBuilder(Context context) {
        this.context = context;
    }

    public boolean buildApk(AppIdeaModel appIdea, File projectRoot, File outputDir) {
        try {
            File buildTools = new File(context.getFilesDir(), "build-tools");

            File manifestFile = new File(projectRoot, "AndroidManifest.xml");
            File resDir = new File(projectRoot, "res");
            File javaSrc = new File(projectRoot, "src");
            File classesDex = new File(outputDir, "classes.dex");
            File apkUnsigned = new File(outputDir, "app-unsigned.apk");
            File apkAligned = new File(outputDir, "app-aligned.apk");
            File apkFinal = new File(outputDir, appIdea.getAppName().replaceAll("\\s+", "") + ".apk");

            File aapt2 = new File(buildTools, "aapt2");
            File d8 = new File(buildTools, "d8");
            File zipalign = new File(buildTools, "zipalign");
            File apksigner = new File(buildTools, "apksigner");
            File androidJar = new File(buildTools, "android.jar");
            File keystore = new File(buildTools, "dev_keystore.jks");

            Log.i(TAG, "📦 Starting build for: " + appIdea.getAppName());

            // Step 1: Compile resources
            Log.i(TAG, "🔧 Compiling resources...");
            Process compileRes = new ProcessBuilder(
                    aapt2.getAbsolutePath(), "compile",
                    "--dir", resDir.getAbsolutePath(),
                    "-o", new File(outputDir, "compiled-res.zip").getAbsolutePath()
            ).redirectErrorStream(true).start();
            compileRes.waitFor();

            // Step 2: Link resources
            Log.i(TAG, "🔗 Linking resources...");
            Process linkRes = new ProcessBuilder(
                    aapt2.getAbsolutePath(), "link",
                    "-o", apkUnsigned.getAbsolutePath(),
                    "--manifest", manifestFile.getAbsolutePath(),
                    "-I", androidJar.getAbsolutePath(),
                    "--java", javaSrc.getAbsolutePath(),
                    "--proto-format",
                    new File(outputDir, "compiled-res.zip").getAbsolutePath()
            ).redirectErrorStream(true).start();
            linkRes.waitFor();

            // Step 3: Compile Java to DEX
            Log.i(TAG, "📦 Converting Java to .dex...");
            Process compileJava = new ProcessBuilder(
                    d8.getAbsolutePath(),
                    javaSrc.getAbsolutePath(),
                    "--lib", androidJar.getAbsolutePath(),
                    "--output", outputDir.getAbsolutePath()
            ).redirectErrorStream(true).start();
            compileJava.waitFor();

            if (!classesDex.exists()) {
                Log.e(TAG, "❌ Dex file not created.");
                return false;
            }

            // Step 4: Merge dex into APK
            Log.i(TAG, "📥 Merging .dex into APK...");
            if (!Utils.mergeDexIntoApk(classesDex, apkUnsigned)) {
                Log.e(TAG, "❌ Failed to merge dex into APK.");
                return false;
            }

            // Step 5: Align APK
            Log.i(TAG, "📏 Aligning APK...");
            Process align = new ProcessBuilder(
                    zipalign.getAbsolutePath(),
                    "-p", "4",
                    apkUnsigned.getAbsolutePath(),
                    apkAligned.getAbsolutePath()
            ).redirectErrorStream(true).start();
            align.waitFor();

            if (!apkAligned.exists()) {
                Log.e(TAG, "❌ APK alignment failed.");
                return false;
            }

            // Step 6: Sign APK
            Log.i(TAG, "🖊️ Signing APK...");
            Process sign = new ProcessBuilder(
                    apksigner.getAbsolutePath(),
                    "sign",
                    "--ks", keystore.getAbsolutePath(),
                    "--ks-key-alias", "devkey",
                    "--ks-pass", "pass:devpass",
                    "--key-pass", "pass:devpass",
                    "--out", apkFinal.getAbsolutePath(),
                    apkAligned.getAbsolutePath()
            ).redirectErrorStream(true).start();
            sign.waitFor();

            if (!apkFinal.exists()) {
                Log.e(TAG, "❌ Final APK not found after signing.");
                return false;
            }

            Log.i(TAG, "✅ Final APK built: " + apkFinal.getAbsolutePath());
            return true;

        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "❌ Build failed with exception", e);
            return false;
        }
    }
}