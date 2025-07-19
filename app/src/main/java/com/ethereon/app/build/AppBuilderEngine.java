package com.ethereon.app.build;

import android.content.Context;
import android.util.Log;

import com.ethereon.app.models.AppIdeaModel;
import com.ethereon.app.models.AppBlueprint;
import com.ethereon.app.pipeline.AppBlueprintGenerator;
import com.ethereon.app.pipeline.ProjectFileManager;

import java.io.File;

public class AppBuilderEngine {

    private static final String TAG = "AppBuilderEngine";
    private final Context context;
    private final AppBlueprintGenerator blueprintGenerator;
    private final ProjectFileManager fileManager;

    public AppBuilderEngine(Context context) {
        this.context = context;
        this.blueprintGenerator = new AppBlueprintGenerator();
        this.fileManager = new ProjectFileManager(context);
    }

    /**
     * Builds a fully structured Android app project from an idea.
     */
    public boolean buildFromIdea(AppIdeaModel idea, File outputDirectory) {
        Log.i(TAG, "Generating blueprint for app idea: " + idea.getAppName());

        AppBlueprint blueprint = blueprintGenerator.generateBlueprint(idea);
        if (blueprint == null) {
            Log.e(TAG, "Failed to generate blueprint.");
            return false;
        }

        Log.i(TAG, "Creating project files in: " + outputDirectory.getAbsolutePath());

        boolean success = fileManager.createProjectStructure(outputDirectory, blueprint);
        if (!success) {
            Log.e(TAG, "Failed to create project structure.");
            return false;
        }

        Log.i(TAG, "Project generation complete.");
        return true;
    }
}