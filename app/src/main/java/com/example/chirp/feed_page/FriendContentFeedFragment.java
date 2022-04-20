package com.example.chirp.feed_page;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chirp.R;
import com.example.chirp.posts.ModelPost;
import com.example.chirp.trending_page.HomePostsAdapter;
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
import java.util.Map;


public class FriendContentFeedFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ModelPost> posts = new ArrayList<>();
    ArrayList<String> friends = new ArrayList<>();
    FriendContentFeedPostsAdapter adapter;
    String TAG = "FRIENDSFEED";

    public FriendContentFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        // GET FOLLOWS
        DatabaseReference friendsref = db.child("FriendRequests").child(firebaseUser.getUid());
        DatabaseReference postsref = db.child("posts");

        if(savedInstanceState == null || !savedInstanceState.containsKey("friendPosts")) {
            posts = new ArrayList<>();
        } else {
            posts = savedInstanceState.getParcelableArrayList("friendPosts");
        }

        ValueEventListener postsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost post = ds.getValue(ModelPost.class);

                    if(friends.contains(post.userID)) {
                        posts.add(post);
                    }
                }
                Collections.reverse(posts);
                adapter = new FriendContentFeedPostsAdapter(getContext(), posts);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,"Read Failed: " + error.getCode());
            }
        };
        friendsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> td = (HashMap<String, Object>) ds.getValue();

                    for(Map.Entry<String, Object> entry : td.entrySet()) {
                        if(entry.getValue().toString().equals("accepted")) {
                            friends.add(ds.getKey());
                        }
                    }
                }
                Log.d(TAG, friends.toString());
                postsref.addListenerForSingleValueEvent(postsEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postsref.addValueEventListener(postsEventListener);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("friendPosts", posts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_content_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) getView().findViewById(R.id.friendContentFeedPostsRecycler);
        adapter = new FriendContentFeedPostsAdapter(getContext(), posts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        super.onViewCreated(view, savedInstanceState);
    }
}