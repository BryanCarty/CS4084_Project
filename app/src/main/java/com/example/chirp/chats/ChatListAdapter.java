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

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>{

    private final Context context;
    private final List<ChatListModel> chatListModelList;

    public ChatListAdapter(Context context, List<ChatListModel> chatListModelList) {
        this.context = context;
        this.chatListModelList = chatListModelList;
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_layout, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListViewHolder holder, int position) {

        ChatListModel chatListModel = chatListModelList.get(position);

        holder.textFullName.setText(chatListModel.getUserName());

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

        String lastMessage  = chatListModel.getLastMessage();
        lastMessage = lastMessage.length()>30?lastMessage.substring(0,30):lastMessage;
        holder.textLastMessage.setText(lastMessage);

        String lastMessageTime = chatListModel.getLastMessageTime();
        if(lastMessageTime==null) lastMessageTime="";
        if(!TextUtils.isEmpty(lastMessageTime))
            holder.textLastMessageTime.setText(Util.getTimeAgo(Long.parseLong(lastMessageTime)));

        if(!chatListModel.getUnreadCount().equals("0"))
        {
            holder.textUnreadCount.setVisibility(View.VISIBLE);
            holder.textUnreadCount.setText(chatListModel.getUnreadCount());
        }
        else
            holder.textUnreadCount.setVisibility(View.GONE);

        holder.llChatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(Extras.USER_KEY, chatListModel.getUserId());
                intent.putExtra(Extras.USER_NAME, chatListModel.getUserName());
                intent.putExtra(Extras.PHOTO_NAME, chatListModel.getPhotoName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatListModelList.size();
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout llChatList;
        private final TextView textFullName;
        private final TextView textLastMessage;
        private final TextView textLastMessageTime;
        private final TextView textUnreadCount;
        private final ImageView ivProfile;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            llChatList = itemView.findViewById(R.id.llChatList);
            textFullName = itemView.findViewById(R.id.textFullName);
            textLastMessage = itemView.findViewById(R.id.textLastMessage);
            textLastMessageTime = itemView.findViewById(R.id.textLastMessageTime);
            textUnreadCount = itemView.findViewById(R.id.textUnreadCount);
            ivProfile = itemView.findViewById(R.id.ivProfile);
        }
    }
}

