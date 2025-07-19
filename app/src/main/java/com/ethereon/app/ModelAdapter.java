package com.ethereon.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ModelViewHolder> {

    private final List<ModelItem> modelList;
    private final OnModelClickListener listener;

    public interface OnModelClickListener {
        void onModelClick(ModelItem modelItem);
    }

    public ModelAdapter(List<ModelItem> modelList, OnModelClickListener listener) {
        this.modelList = modelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item, parent, false);
        return new ModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelViewHolder holder, int position) {
        ModelItem modelItem = modelList.get(position);
        holder.bind(modelItem, listener);
    }

    @Override
    public int getItemCount() {
        return modelList != null ? modelList.size() : 0;
    }

    static class ModelViewHolder extends RecyclerView.ViewHolder {
        TextView textViewModelName;

        public ModelViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewModelName = itemView.findViewById(R.id.textViewModelName);
        }

        public void bind(final ModelItem modelItem, final OnModelClickListener listener) {
            textViewModelName.setText(modelItem.getName());
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onModelClick(modelItem);
                }
            });
        }
    }
}