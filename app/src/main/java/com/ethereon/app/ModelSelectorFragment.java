package com.ethereon.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

public class ModelSelectorFragment extends Fragment {

    private static final int PICK_MODEL_FILE = 101;
    private TextView txtModelPath;
    private ModelHandler modelHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_model_selector, container, false);

        txtModelPath = view.findViewById(R.id.txtModelPath);
        Button btnSelectModel = view.findViewById(R.id.btnSelectModelFile);

        modelHandler = new ModelHandler(requireContext());

        btnSelectModel.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select AI Model"), PICK_MODEL_FILE);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_MODEL_FILE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri == null) {
                txtModelPath.setText("Invalid URI received.");
                return;
            }

            String path = FileUtils.getPath(requireContext(), uri);

            if (path != null) {
                File modelFile = new File(path);
                if (modelFile.exists() && modelFile.isFile()) {
                    modelHandler.setModelFile(modelFile);
                    txtModelPath.setText("Model loaded:\n" + modelFile.getAbsolutePath());
                    Toast.makeText(getContext(), "Model file selected", Toast.LENGTH_SHORT).show();
                } else {
                    txtModelPath.setText("Selected file does not exist or is invalid.");
                }
            } else {
                txtModelPath.setText("Unable to resolve file path.");
            }
        }
    }
}