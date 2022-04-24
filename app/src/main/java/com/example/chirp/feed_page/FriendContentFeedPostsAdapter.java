package com.example.chirp.feed_page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chirp.R;
import com.example.chirp.posts.ModelPost;

import java.util.List;

public class FriendContentFeedPostsAdapter extends RecyclerView.Adapter<FriendContentFeedPostsAdapter.PostViewHolder> {

    List<ModelPost> posts;
    Context context;

    public FriendContentFeedPostsAdapter(Context ct, List<ModelPost> posts) {
        context = ct;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_row, parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        ModelPost post = posts.get(position); // Gets the current post

        holder.postDisplayName.setText(post.getUserName());
        holder.postTitle.setText(post.getTitle());
        holder.postContent.setText(post.getContent());
        holder.postReplyCount.setText(String.format("Replies: %d", post.getReplyCount()));
        holder.postReplyCount.setTag(post.getReplyCount());

        // Logic for formatting the time since the post was sent
        Long timeSentInMillis = post.getTimeSent()*1000;
        Long currentTimeInMillis = System.currentTimeMillis();
        Long diff = currentTimeInMillis - timeSentInMillis;
        Long diffInSeconds = diff / 1000;
        Long diffInMinutes = diffInSeconds / 60;
        Long diffInHours = diffInMinutes / 60;
        Long diffInDays = diffInHours / 24;
        if(diffInSeconds < 60) {
            holder.postTimeSent.setText(String.format("%ds", diffInSeconds)); // seconds
        } else if(diffInMinutes< 60) {
            holder.postTimeSent.setText(String.format("%dm", diffInMinutes)); // minutes
        } else if(diffInHours < 24) {
            holder.postTimeSent.setText(String.format("%dh", diffInHours)); // hours
        } else if(diffInDays < 365) {
            holder.postTimeSent.setText(String.format("%dd", diffInDays)); // days
        }

        Glide.with(holder.itemView).load(post.getUserImage()).into(holder.postProfileImage); // load profile image of post
        holder.postProfileImage.setTag(post.getUserImage()); // store the url of the profile image for future access
        holder.postTitle.setTag(post.getPostID());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

        TextView postTitle, postContent, postTimeSent, postDisplayName, postReplyCount;
        ImageView postProfileImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postContent = itemView.findViewById(R.id.postContent);
            postTimeSent = itemView.findViewById(R.id.postTimeSent);
            postDisplayName = itemView.findViewById(R.id.postDisplayName);
            postProfileImage = itemView.findViewById(R.id.postProfileImage);
            postReplyCount = itemView.findViewById(R.id.repliesCount);
        }
    }
}
