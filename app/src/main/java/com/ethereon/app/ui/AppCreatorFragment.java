package com.ethereon.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ethereon.app.R;
import com.ethereon.app.build.AppBuilderEngine;
import com.ethereon.app.build.FinalApkBuilder;
import com.ethereon.app.models.AppIdeaModel;
import com.ethereon.app.utils.Utils;

import java.io.File;

public class AppCreatorFragment extends Fragment {

    private EditText editAppName, editAppIdea;
    private Button btnGenerateApp, btnBuildApp;
    private File projectDir;

    public AppCreatorFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_creator, container, false);

        editAppName = view.findViewById(R.id.editAppName);
        editAppIdea = view.findViewById(R.id.editAppIdea);
        btnGenerateApp = view.findViewById(R.id.btnGenerateApp);
        btnBuildApp = view.findViewById(R.id.btnBuildApp);

        btnGenerateApp.setOnClickListener(v -> generateApp());
        btnBuildApp.setOnClickListener(v -> buildApk());

        return view;
    }

    private void generateApp() {
        String name = editAppName.getText().toString().trim();
        String idea = editAppIdea.getText().toString().trim();

        if (name.isEmpty() || idea.isEmpty()) {
            Utils.showToast(requireContext(), "Please enter both app name and idea.");
            return;
        }

        AppIdeaModel model = new AppIdeaModel(name, idea);
        projectDir = new File(Utils.getAppStorageDirectory(requireContext()), name);

        if (!projectDir.exists()) projectDir.mkdirs();

        AppBuilderEngine builder = new AppBuilderEngine(requireContext());
        boolean result = builder.generateStarterFiles(model, projectDir);

        Utils.showToast(requireContext(), result ? "Starter files generated." : "Generation failed.");
    }

    private void buildApk() {
        if (projectDir == null || !projectDir.exists()) {
            Utils.showToast(requireContext(), "Generate the app files first.");
            return;
        }

        FinalApkBuilder builder = new FinalApkBuilder(requireContext());
        boolean result = builder.buildApkFromDirectory(projectDir);

        Utils.showToast(requireContext(), result ? "APK build complete!" : "Build failed.");
    }
}