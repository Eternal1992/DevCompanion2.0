// File: BuildStatusCallback.java
package com.ethereon.app.interfaces;

public interface BuildStatusCallback {

    // Called when the build starts
    void onBuildStarted();

    // Called periodically with status updates or progress messages
    void onBuildProgress(String message);

    // Called when the build finishes successfully
    void onBuildSuccess(String apkPath);

    // Called when the build fails
    void onBuildFailure(String errorMessage);
}