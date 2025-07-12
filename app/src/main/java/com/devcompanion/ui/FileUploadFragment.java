package com.devcompanion.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.devcompanion.R;

import java.io.IOException;

public class FileUploadFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView previewImageView;
    private Button selectImageButton;
    private Button sendToAIButton;

    private Uri selectedImageUri;

    public FileUploadFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_upload, container, false);

        previewImageView = view.findViewById(R.id.image_preview);
        selectImageButton = view.findViewById(R.id.button_select_image);
        sendToAIButton = view.findViewById(R.id.button_send_ai);

        sendToAIButton.setEnabled(false);

        selectImageButton.setOnClickListener(v -> openImageChooser());

        sendToAIButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                // TODO: Add AI analysis logic here for the image
                Toast.makeText(getContext(), "Sending image to AI (simulated)", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                previewImageView.setImageBitmap(bitmap);
                sendToAIButton.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}