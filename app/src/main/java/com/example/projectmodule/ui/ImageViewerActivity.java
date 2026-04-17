package com.example.projectmodule.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectmodule.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class ImageViewerActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_RES_ID = "extra_image_res_id";
    public static final String EXTRA_IMAGE_TITLE = "extra_image_title";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        int imageResId = getIntent().getIntExtra(EXTRA_IMAGE_RES_ID, R.drawable.ic_menu_compass);
        String title = getIntent().getStringExtra(EXTRA_IMAGE_TITLE);

        ZoomableImageView imageView = findViewById(R.id.fullScreenImageView);
        MaterialTextView titleView = findViewById(R.id.fullScreenTitle);
        MaterialButton closeButton = findViewById(R.id.closeButton);

        titleView.setText(title != null ? title : getString(R.string.title_image_fullscreen));
        imageView.setImageResource(imageResId);
        imageView.setOnClickListener(v -> finish());
        closeButton.setOnClickListener(v -> finish());
    }
}

