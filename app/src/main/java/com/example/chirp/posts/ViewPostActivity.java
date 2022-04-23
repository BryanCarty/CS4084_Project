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

        // Obtain fields to fill, and update them with the details from the Intent
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
        recyclerView = (RecyclerView) findViewById(R.id.repliesRecycler); // Instantiate recycler view for replies
        adapter = new RepliesAdapter(ViewPostActivity.this, replies);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewPostActivity.this));

        // Define event listener for replies
        ValueEventListener repliesEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                replies.clear(); // clear previous list of replies
                for(DataSnapshot ds : snapshot.getChildren()) { // for every reply under the selected post
                    ModelPost reply = ds.getValue(ModelPost.class); // parse the reply to a ModelPost object
                    replies.add(reply); // then add the replies to the list
                }
                Collections.reverse(replies); // order replies by the most recent
                adapter = new RepliesAdapter(ViewPostActivity.this, replies); // instantiate the adapter using list of replies
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ViewPostActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("REPLIES", "Failed to load replies " + error.getDetails());
            }
        };

        repliesRef.addValueEventListener(repliesEventListener);
    }

    /**
     *
     * The onclick function for the "REPLY" Button. Will organise the reply details
     * and submit the reply to Firebase
     *
     * @param view
     */
    public void btnMakeReply(View view) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        TextView postTitle = findViewById(R.id.postTitle); // Title of the original post
        TextInputEditText replyContent = findViewById(R.id.postReplyContent); // content of the user's reply

        // Error handling to prevent empty replies being accepted
        if (replyContent.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter Reply description", Toast.LENGTH_LONG).show();
        } else {
            // Hashmap to hold details of the reply
            HashMap<String, Object> reply = new HashMap();
            reply.put("title", "Re: " + postTitle.getText().toString());
            reply.put("content", replyContent.getText().toString());
            reply.put("timeSent", Timestamp.now().getSeconds());
            reply.put("userID", firebaseUser.getUid());
            reply.put("userImage", firebaseUser.getPhotoUrl().toString());
            reply.put("userName", firebaseUser.getDisplayName());

            // Create a new instance on Firebase to hold the reply details
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
