package com.devcompanion.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devcompanion.R;

public class RoadmapFragment extends Fragment {

    public RoadmapFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roadmap, container, false);

        TextView roadmapText = view.findViewById(R.id.roadmap_text);

        // Example roadmap text, update as needed
        String roadmap = "App Roadmap:\n\n" +
                "1. Stable chat with local database memory\n" +
                "2. AI integration for code generation\n" +
                "3. File upload & AI analysis\n" +
                "4. UI polish & customization\n" +
                "5. Add project export and GitHub sync\n" +
                "6. Future public version and monetization\n";

        roadmapText.setText(roadmap);

        return view;
    }
}