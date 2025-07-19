package com.ethereon.app;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProjectDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProjectDetailActivity";

    private TextView projectName, projectStatus, projectProgressText;
    private ProgressBar projectProgressBar;
    private RecyclerView todoRecycler;
    private TodoAdapter todoAdapter;
    private final List<TodoItem> todoList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        // Bind UI elements
        projectName = findViewById(R.id.projectName);
        projectStatus = findViewById(R.id.projectStatus);
        projectProgressText = findViewById(R.id.projectProgressText);
        projectProgressBar = findViewById(R.id.projectProgressBar);
        todoRecycler = findViewById(R.id.todoRecycler);

        // Setup RecyclerView
        todoRecycler.setLayoutManager(new LinearLayoutManager(this));
        todoAdapter = new TodoAdapter(todoList);
        todoRecycler.setAdapter(todoAdapter);

        // Load project passed in from intent
        String project = getIntent().getStringExtra("project");
        if (project != null && !project.trim().isEmpty()) {
            loadProjectData(project.trim());
        } else {
            Log.e(TAG, "No project name provided in intent");
        }
    }

    /**
     * Loads data from todo.json asset file and populates the UI
     */
    private void loadProjectData(String projectNameStr) {
        try {
            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open("todo.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject root = new JSONObject(json);
            JSONArray projects = root.getJSONArray("projects");

            for (int i = 0; i < projects.length(); i++) {
                JSONObject obj = projects.getJSONObject(i);
                String name = obj.getString("name");

                if (name.equalsIgnoreCase(projectNameStr)) {
                    // Update basic project details
                    String status = obj.getString("status");
                    int progress = obj.getInt("progress");

                    projectName.setText(name);
                    projectStatus.setText(status);
                    projectProgressText.setText(progress + "%");
                    projectProgressBar.setProgress(progress);

                    // Update to-do list
                    JSONArray tasks = obj.getJSONArray("todo");
                    todoList.clear();
                    for (int j = 0; j < tasks.length(); j++) {
                        JSONObject task = tasks.getJSONObject(j);
                        String taskName = task.getString("task");
                        boolean done = task.getBoolean("done");
                        todoList.add(new TodoItem(taskName, done));
                    }
                    todoAdapter.notifyDataSetChanged();
                    return;
                }
            }

            Log.w(TAG, "Project not found in JSON: " + projectNameStr);

        } catch (Exception e) {
            Log.e(TAG, "Error loading project data", e);
        }
    }
}