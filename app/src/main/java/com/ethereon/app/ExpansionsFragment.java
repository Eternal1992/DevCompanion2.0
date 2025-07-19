// File: ExpansionsFragment.java
package com.ethereon.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.List;

public class ExpansionsFragment extends Fragment {

    private ListView listView;
    private Button btnCheckUpdates;
    private ArrayAdapter<String> adapter;
    private ExpansionManager expansionManager;
    private List<File> expansions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expansions, container, false);

        listView = view.findViewById(R.id.expansionList);
        btnCheckUpdates = view.findViewById(R.id.btnCheckUpdates);

        expansionManager = new ExpansionManager(requireContext());

        btnCheckUpdates.setOnClickListener(v -> refreshExpansionList());

        refreshExpansionList();

        return view;
    }

    private void refreshExpansionList() {
        expansions = expansionManager.scanForExpansions();

        List<String> names = expansions.stream()
                .map(File::getName)
                .toList();

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            File selected = expansions.get(position);
            boolean success = expansionManager.installExpansion(selected);
            Toast.makeText(getContext(),
                    success ? "Installed: " + selected.getName() : "Failed to install.",
                    Toast.LENGTH_SHORT).show();
        });
    }
}