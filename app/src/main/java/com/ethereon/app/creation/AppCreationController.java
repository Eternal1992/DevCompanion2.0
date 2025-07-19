package com.ethereon.app.creation;

import android.content.Context;
import android.util.Log;

import com.ethereon.app.models.AppIdeaModel;
import com.ethereon.app.utils.ProjectScaffolder;

public class AppCreationController {

    private static final String TAG = "AppCreationController";
    private final Context context;

    public AppCreationController(Context context) {
        this.context = context;
    }

    public void createAppFromIdea(String ideaText) {
        Log.i(TAG, "Received app idea: " + ideaText);

        // Parse idea (later can use LLM or custom NLP logic)
        AppIdeaModel idea = parseIdeaText(ideaText);

        // Scaffold basic project layout
        ProjectScaffolder scaffolder = new ProjectScaffolder(context);
        boolean success = scaffolder.generateProjectStructure(idea);

        if (success) {
            Log.i(TAG, "App creation completed for: " + idea.getAppName());
        } else {
            Log.e(TAG, "App creation failed.");
        }
    }

    private AppIdeaModel parseIdeaText(String text) {
        // Placeholder parser for now
        String appName = "MyApp";
        if (text.toLowerCase().contains("notes")) appName = "NovaNotes";
        else if (text.toLowerCase().contains("todo")) appName = "TaskNova";
        else if (text.toLowerCase().contains("chat")) appName = "ChatterNova";

        AppIdeaModel idea = new AppIdeaModel();
        idea.setAppName(appName);
        idea.addFeature("Basic UI");
        idea.addFeature("Single Activity");

        if (text.toLowerCase().contains("search")) idea.addFeature("Search Bar");
        if (text.toLowerCase().contains("dark")) idea.addFeature("Dark Mode");

        return idea;
    }
}