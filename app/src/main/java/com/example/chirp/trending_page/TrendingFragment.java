package com.example.chirp.trending_page;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chirp.R;
import com.example.chirp.posts.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class TrendingFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ModelPost> posts;
    HomePostsAdapter adapter;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference postsref = db.child("posts");
    ValueEventListener postValueListener;
    String TAG = "TRENDING";
    Iterable<DataSnapshot> allPosts;

    public TrendingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Define the event listener for new posts from Firebase
        postValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear(); // Clear out the previous list
                allPosts = snapshot.getChildren();
                DatabaseReference repliesref = db.child("replies");
                repliesref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        for(DataSnapshot ds : allPosts) { // For every child in the snapshot (every post)
                            ModelPost post = ds.getValue(ModelPost.class); // parse the post into the ModelPost class
                            post.setPostID(ds.getKey());
                            post.setReplyCount(snap.child(ds.getKey()).getChildrenCount());
                            posts.add(post);
                        }

                        Collections.reverse(posts); // order posts by most recently posted
                        adapter = new HomePostsAdapter(getContext(), posts); // pass posts list to the adapter
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG,"Failed to retrieve Post reply count: " + error.getDetails());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,"Read Failed: " + error.getDetails());
            }
        };

        // Was data saved previously or is this a fresh boot
        if(savedInstanceState == null || !savedInstanceState.containsKey("posts")) {
            posts = new ArrayList<>();

            postsref.addValueEventListener(postValueListener);
        } else {
            posts = savedInstanceState.getParcelableArrayList("posts");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("posts", posts); // Save posts list upon destruction of fragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trending, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) getView().findViewById(R.id.homePostsRecycler); // Instantiate recycler view for posts
        adapter = new HomePostsAdapter(getContext(), posts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        postsref.removeEventListener(postValueListener);
    }
}