package com.ethereon.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ethereon.app.R;

public class BuildProgressFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView txtStatus;
    private TextView txtLog;

    private static BuildProgressFragment instance;

    public static BuildProgressFragment getInstance() {
        if (instance == null) {
            instance = new BuildProgressFragment();
        }
        return instance;
    }

    public BuildProgressFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_build_progress, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        txtStatus = view.findViewById(R.id.txtStatus);
        txtLog = view.findViewById(R.id.txtLog);
        return view;
    }

    public void updateProgress(int progress, String status, String log) {
        if (progressBar != null) progressBar.setProgress(progress);
        if (txtStatus != null) txtStatus.setText(status);
        if (txtLog != null) txtLog.append(log + "\n");
    }

    public void reset() {
        if (progressBar != null) progressBar.setProgress(0);
        if (txtStatus != null) txtStatus.setText("Idle");
        if (txtLog != null) txtLog.setText("");
    }
}