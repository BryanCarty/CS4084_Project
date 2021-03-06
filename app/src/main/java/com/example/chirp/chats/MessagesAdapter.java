package com.example.chirp.chats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chirp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>{

    private final Context context;
    private final List<MessageModel> messageList;

    public MessagesAdapter(Context context, List<MessageModel> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    /* Used to inflate the message layout to show the messages*/
    @NonNull
    @Override
    public MessagesAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_layout, parent, false);
        return new MessageViewHolder(view);
    }

    /* Binds the relevant data to the message layout */
    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MessageViewHolder holder, int position) {

        MessageModel message = messageList.get(position);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        String fromUserId = message.getMessageFrom();
        /* Used to grab the timestamp for the message */
        SimpleDateFormat sfd = new SimpleDateFormat( "dd-MM-yyyy HH:mm");

        String datetime = sfd.format(new Date(message.getMessageTime()));
        String [] splitString = datetime.split(" ");
        /* Used to get the time, not the date */
        String messageTime = splitString[1];

        /* Checks if it is a sent message, hides the received message portion  and vice verse */
        if(fromUserId.equals(currentUserId)){
            holder.llSent.setVisibility(View.VISIBLE);
            holder.llReceived.setVisibility(View.GONE);
            holder.tvSentMessage.setText(message.getMessage());
            holder.tvSentMessageTime.setText(messageTime);
        }
        else
        {
            holder.llSent.setVisibility(View.GONE);
            holder.llReceived.setVisibility(View.VISIBLE);
            holder.tvReceivedMessage.setText(message.getMessage());
            holder.tvReceivedMessageTime.setText(messageTime);
        }
    }

    /* Returns the size of the message list */
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    /* Declares/initialises all the views that are present in the message layout xml file */
    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        private final LinearLayout llSent;
        private final LinearLayout llReceived;
        private final TextView tvSentMessage;
        private final TextView tvSentMessageTime;
        private final TextView tvReceivedMessage;
        private final TextView tvReceivedMessageTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            llSent = itemView.findViewById(R.id.llSent);
            llReceived = itemView.findViewById(R.id.llReceived);
            tvSentMessage = itemView.findViewById(R.id.tvSentMessage);
            tvSentMessageTime = itemView.findViewById(R.id.tvSentMessageTime);

            tvReceivedMessage = itemView.findViewById(R.id.tvReceivedMessage);
            tvReceivedMessageTime = itemView.findViewById(R.id.tvReceivedMessageTime);

        }
    }
}
