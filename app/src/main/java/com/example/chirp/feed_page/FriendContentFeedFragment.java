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
    ArrayList<ModelPost> posts;
    ArrayList<String> friends = new ArrayList<>();
    FriendContentFeedPostsAdapter adapter;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference friendsref;
    DatabaseReference postsref;
    DatabaseReference repliesref = db.child("replies");
    ValueEventListener friendsEventListener;
    ValueEventListener postsEventListener;
    String TAG = "FRIENDSFEED";
    Iterable<DataSnapshot> allPosts;
    boolean doReverse = true;

    public FriendContentFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // GET FOLLOWS
        friendsref = db.child("FriendRequests").child(firebaseUser.getUid());
        postsref = db.child("posts");

        // Define the event listener that listens for new posts published to firebase
        postsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                allPosts = snapshot.getChildren();

                repliesref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        for(DataSnapshot ds : allPosts) {
                            ModelPost post = ds.getValue(ModelPost.class);
                            post.setPostID(ds.getKey());
                            post.setReplyCount(snap.child(ds.getKey()).getChildrenCount());

                            if(friends.contains(post.userID)) {
                                posts.add(post);
                            }
                        }

                        if(doReverse) {
                            Collections.reverse(posts);
                            adapter = new FriendContentFeedPostsAdapter(getContext(), posts);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            doReverse = false;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG,"Failed to retrieve Post reply count: " + error.getDetails());
                    }
                });
                doReverse = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,"Read Failed: " + error.getCode());
            }
        };

        // Define the event listener that listens for new friends made by the user
        friendsEventListener = new ValueEventListener() {
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
        };

        // Logic to handle the potential destruction of the fragment.
        if(savedInstanceState == null || !savedInstanceState.containsKey("friendPosts")) { // There is no saved data -> Likely this is a fresh start
            posts = new ArrayList<>();

            friendsref.addValueEventListener(friendsEventListener);
            postsref.addValueEventListener(postsEventListener);
        } else { // There was data saved previously -> Restore it
            posts = savedInstanceState.getParcelableArrayList("friendPosts");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("friendPosts", posts); // Save the arrayList of posts when the fragment is destroyed
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_content_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) getView().findViewById(R.id.friendContentFeedPostsRecycler); // Instantiate the recycler view for posts
        adapter = new FriendContentFeedPostsAdapter(getContext(), posts);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove event listeners
        postsref.removeEventListener(postsEventListener);
        friendsref.removeEventListener(friendsEventListener);
    }
}