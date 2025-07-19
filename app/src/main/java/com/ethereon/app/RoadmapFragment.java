package com.ethereon.app;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RoadmapFragment extends Fragment {

    private RecyclerView roadmapList;
    private RoadmapAdapter adapter;
    private final List<RoadmapItem> projectList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roadmap, container, false);

        roadmapList = view.findViewById(R.id.recyclerRoadmap);
        roadmapList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoadmapAdapter(projectList);
        roadmapList.setAdapter(adapter);

        loadRoadmapData();

        return view;
    }

    private void loadRoadmapData() {
        projectList.clear();

        try {
            AssetManager assetManager = requireContext().getAssets();
            InputStream is = assetManager.open("roadmap.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject root = new JSONObject(json);
            JSONArray projects = root.getJSONArray("projects");

            for (int i = 0; i < projects.length(); i++) {
                JSONObject obj = projects.getJSONObject(i);
                String name = obj.getString("name");
                String status = obj.getString("status");
                int progress = obj.getInt("progress");
                projectList.add(new RoadmapItem(name, progress, status));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
    }

    public static class RoadmapItem {
        public final String name;
        public final int progress;
        public final String status;

        public RoadmapItem(String name, int progress, String status) {
            this.name = name;
            this.progress = progress;
            this.status = status;
        }
    }

    public class RoadmapAdapter extends RecyclerView.Adapter<RoadmapAdapter.ViewHolder> {
        private final List<RoadmapItem> items;

        public RoadmapAdapter(List<RoadmapItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_roadmap, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RoadmapItem item = items.get(position);
            holder.name.setText(item.name);
            holder.status.setText(item.status);
            holder.progress.setText(item.progress + "%");
            holder.progressBar.setProgress(item.progress);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, status, progress;
            ProgressBar progressBar;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.projectName);
                status = itemView.findViewById(R.id.projectStatus);
                progress = itemView.findViewById(R.id.projectProgressText);
                progressBar = itemView.findViewById(R.id.projectProgressBar);
            }

            void bind(RoadmapItem item) {
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), ProjectDetailActivity.class);
                    intent.putExtra("project_name", item.name);
                    intent.putExtra("project_status", item.status);
                    intent.putExtra("project_progress", item.progress);
                    startActivity(intent);
                });
            }
        }
    }
}