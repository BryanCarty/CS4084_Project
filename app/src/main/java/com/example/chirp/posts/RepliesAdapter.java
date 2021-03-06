package com.example.chirp.posts;

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

import java.util.List;

public class RepliesAdapter extends RecyclerView.Adapter<com.example.chirp.posts.RepliesAdapter.PostViewHolder> {

    List<ModelPost> posts;
    Context context;

    public  RepliesAdapter(Context ct, List<ModelPost> posts) {
        context = ct;
        this.posts = posts;
    }

    @NonNull
    @Override
    public com.example.chirp.posts.RepliesAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.reply_row, parent,false);
        return new com.example.chirp.posts.RepliesAdapter.PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.chirp.posts.RepliesAdapter.PostViewHolder holder, int position) {
        ModelPost post = posts.get(position); // Gets the current post

        holder.postDisplayName.setText(post.getUserName());
        holder.postContent.setText(post.getContent());

        // Logic for formatting the time since the post was published
        Long timeSentInMillis = post.getTimeSent()*1000;
        Long currentTimeInMillis = System.currentTimeMillis();
        Long diff = currentTimeInMillis - timeSentInMillis;
        Long diffInSeconds = diff / 1000;
        Long diffInMinutes = diffInSeconds / 60;
        Long diffInHours = diffInMinutes / 60;
        Long diffInDays = diffInHours / 24;
        if(diffInSeconds < 60) {
            holder.postTimeSent.setText(String.format("%ds", diffInSeconds));
        } else if(diffInMinutes< 60) {
            holder.postTimeSent.setText(String.format("%dm", diffInMinutes));
        } else if(diffInHours < 24) {
            holder.postTimeSent.setText(String.format("%dh", diffInHours));
        } else if(diffInDays < 365) {
            holder.postTimeSent.setText(String.format("%dd", diffInDays));
        }

        // load the profile image onto the post
        Glide.with(holder.itemView).load(post.getUserImage()).into(holder.postProfileImage);
        holder.postProfileImage.setTag(post.getUserImage()); // store photo url as tag for access.
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{

        TextView postContent, postTimeSent, postDisplayName;
        ImageView postProfileImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postContent = itemView.findViewById(R.id.postContent);
            postTimeSent = itemView.findViewById(R.id.postTimeSent);
            postDisplayName = itemView.findViewById(R.id.postDisplayName);
            postProfileImage = itemView.findViewById(R.id.postProfileImage);
        }
    }
}