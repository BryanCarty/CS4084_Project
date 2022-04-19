package com.example.chirp.posts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chirp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class NewPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
    }

    public void btnMakePost(View view){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        TextInputEditText etTitle = findViewById(R.id.postTitle);
        TextInputEditText etContent = findViewById(R.id.postContent);

        if(etTitle.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter a title", Toast.LENGTH_LONG).show();
        } else if(etContent.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter Post description", Toast.LENGTH_LONG).show();
        } else {
            HashMap post = new HashMap();
            post.put("title", etTitle.getText().toString());
            post.put("content", etContent.getText().toString());
            post.put("timeSent", Timestamp.now().getSeconds());
            post.put("userID", firebaseUser.getUid());
            post.put("userImage", firebaseUser.getPhotoUrl().toString());
            post.put("userName", firebaseUser.getDisplayName());


            DatabaseReference newPostRef = db.child("posts").push();
            newPostRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Chirp Published", Toast.LENGTH_LONG).show();
                    Log.d("NEWPOST", "User has created new post");
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Chirp Failed", Toast.LENGTH_LONG).show();
                }
            });

            finish();
        }


    }
}