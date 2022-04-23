package com.example.chirp.posts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chirp.R;

public class ViewPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        TextView title = findViewById(R.id.postTitle);
        TextView content = findViewById(R.id.postContent);
        TextView displayName = findViewById(R.id.postDisplayName);
        TextView timeSent = findViewById(R.id.postTimeSent);
        ImageView profileImage = findViewById(R.id.postProfileImage);

        title.setText(getIntent().getStringExtra("POST_TITLE"));
        content.setText(getIntent().getStringExtra("POST_CONTENT"));
        displayName.setText(getIntent().getStringExtra("POST_DISPLAY_NAME"));
        timeSent.setText(getIntent().getStringExtra("POST_TIME_SENT"));

        Glide.with(this).load(getIntent().getStringExtra("POST_PROFILE_IMAGE")).into(profileImage);
    }
}
