package com.example.chirp.chats;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chirp.Common.Extras;
import com.example.chirp.Common.Util;
import com.example.chirp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/* Functions as a bridge between the firebase database and the chat list view */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>{

    private final Context context;
    private final List<ChatListModel> chatListModelList;

    public ChatListAdapter(Context context, List<ChatListModel> chatListModelList) {
        this.context = context;
        this.chatListModelList = chatListModelList;
    }

    /* Used to inflate the chat list layout to show chats */
    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_layout, parent, false);
        return new ChatListViewHolder(view);
    }

    /* Binds the relevant data to the chat list layout */
    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListViewHolder holder, int position) {

        ChatListModel chatListModel = chatListModelList.get(position);

        holder.textFullName.setText(chatListModel.getUserName());

        /* Used to get the users profile image url and show it via getDownloadUrl */
        String[] arrSplit = chatListModel.getPhotoName().split("/");
        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("images/"+arrSplit[arrSplit.length-1]);
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(holder.ivProfile);
            }
        });

        /* Used to get the last message time */
        String lastMessageTime = chatListModel.getLastMessageTime();
        if(lastMessageTime==null) lastMessageTime="";
        if(!TextUtils.isEmpty(lastMessageTime))
            holder.textLastMessageTime.setText(Util.getTimeAgo(Long.parseLong(lastMessageTime)));

        /*
         If the unread count is not zero, then it will be displayed
         If it is zero then it is removed/hidden
        */
        if(!chatListModel.getUnreadCount().equals("0"))
        {
            holder.textUnreadCount.setVisibility(View.VISIBLE);
            holder.textUnreadCount.setText(chatListModel.getUnreadCount());
        }
        else
            holder.textUnreadCount.setVisibility(View.GONE);


        /*
         Is used to open the chat activity from anyone clicking on the linear
         layout in the chat list layout
        */
        holder.llChatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Activity is opened via an intent */
                Intent intent = new Intent(context, ChatActivity.class);
                /* Gets the userId,userName and PhotoName from the list when the linear layout is clicked */
                intent.putExtra(Extras.USER_KEY, chatListModel.getUserId());
                intent.putExtra(Extras.USER_NAME, chatListModel.getUserName());
                intent.putExtra(Extras.PHOTO_NAME, chatListModel.getPhotoName());
                context.startActivity(intent);
            }
        });

    }

    /* Returns the size of the chat list */
    @Override
    public int getItemCount() {
        return chatListModelList.size();
    }

    /* Declares/initialises all the views that are present in the chat list layout xml file */
    public static class ChatListViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout llChatList;
        private final TextView textFullName;
        private final TextView textLastMessageTime;
        private final TextView textUnreadCount;
        private final ImageView ivProfile;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            llChatList = itemView.findViewById(R.id.llChatList);
            textFullName = itemView.findViewById(R.id.textFullName);
            textLastMessageTime = itemView.findViewById(R.id.textLastMessageTime);
            textUnreadCount = itemView.findViewById(R.id.textUnreadCount);
            ivProfile = itemView.findViewById(R.id.ivProfile);
        }
    }
}

