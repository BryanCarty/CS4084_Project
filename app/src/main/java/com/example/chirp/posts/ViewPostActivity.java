package com.example.chirp.posts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chirp.R;
import com.example.chirp.trending_page.HomePostsAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ViewPostActivity extends AppCompatActivity {

    ArrayList<ModelPost> replies = new ArrayList<>();
    RepliesAdapter adapter;
    RecyclerView recyclerView;

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

        DatabaseReference repliesRef = FirebaseDatabase.getInstance().getReference().child("replies").child(getIntent().getStringExtra("POST_ID"));
        recyclerView = (RecyclerView) findViewById(R.id.repliesRecycler);
        adapter = new RepliesAdapter(ViewPostActivity.this, replies);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewPostActivity.this));

        ValueEventListener repliesEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                replies.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost post = ds.getValue(ModelPost.class);
                    post.setPostID(ds.getKey());
                    replies.add(post);
                }
                Collections.reverse(replies);
                adapter = new RepliesAdapter(ViewPostActivity.this, replies);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ViewPostActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("REPLIES", "Failed to load replies " + error.getMessage());
            }
        };

        repliesRef.addValueEventListener(repliesEventListener);
    }

    public void btnMakeReply(View view) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        TextView postTitle = findViewById(R.id.postTitle);
        TextInputEditText replyContent = findViewById(R.id.postReplyContent);

        if (replyContent.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter Reply description", Toast.LENGTH_LONG).show();
        } else {
            HashMap<String, Object> reply = new HashMap();
            reply.put("title", "Re: " + postTitle.getText().toString());
            reply.put("content", replyContent.getText().toString());
            reply.put("timeSent", Timestamp.now().getSeconds());
            reply.put("userID", firebaseUser.getUid());
            reply.put("userImage", firebaseUser.getPhotoUrl().toString());
            reply.put("userName", firebaseUser.getDisplayName());


            DatabaseReference newReplyRef = db.child("replies").child(getIntent().getStringExtra("POST_ID")).push();
            newReplyRef.setValue(reply).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Reply Published", Toast.LENGTH_LONG).show();
                    Log.d("NEWREPLY", "User has replied to a post");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Reply Failed", Toast.LENGTH_LONG).show();
                }
            });

        }
    }


}
