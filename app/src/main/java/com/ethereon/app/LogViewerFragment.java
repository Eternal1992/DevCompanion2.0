package com.ethereon.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LogViewerFragment extends Fragment {

    private static final String ARG_TAB_POSITION = "tab_position";
    private int tabPosition;

    public static LogViewerFragment newInstance(int position) {
        LogViewerFragment fragment = new LogViewerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabPosition = getArguments().getInt(ARG_TAB_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_viewer, container, false);
        TextView logOutput = view.findViewById(R.id.logOutput);

        // Stubbed log loading logic based on tab
        switch (tabPosition) {
            case 0: logOutput.setText("UI logs..."); break;
            case 1: logOutput.setText("Build logs..."); break;
            case 2: logOutput.setText("AI logs..."); break;
            case 3: logOutput.setText("General logs..."); break;
        }

        return view;
    }
}