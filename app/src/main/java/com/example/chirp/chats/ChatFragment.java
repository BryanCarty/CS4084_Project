package com.example.chirp.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chirp.Common.NodeNames;
import com.example.chirp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/*
 Queries the firebase database to fetch all the chats and show it on the
 recycler view
*/

public class ChatFragment extends Fragment {

    private RecyclerView rvChatList;
    private View progressBar;
    private TextView tvEmptyChatList;
    private ChatListAdapter chatListAdapter;
    private List<ChatListModel> chatListModelList;

    private DatabaseReference databaseReferenceChats, databaseReferenceUsers;
    private FirebaseUser currentUser;

    /* Want the list will refresh everytime a new value is sent */
    private ChildEventListener childEventListener;
    private Query query;

    private List<String> userIds;

    public ChatFragment() {
        /* Required empty public constructor */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    /* Used to access/initialize views and data */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvChatList = view.findViewById(R.id.rvChats);
        tvEmptyChatList = view.findViewById(R.id.tvEmptyChatList);

        userIds = new ArrayList<>();
        chatListModelList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(getActivity(), chatListModelList);

        /* The data shall be queried in ascending order but displayed in descending order
         * ie the user who sent a chat most recently will be at the top of the list*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvChatList.setLayoutManager(linearLayoutManager);

        rvChatList.setAdapter(chatListAdapter);

        progressBar = view.findViewById(R.id.progressBar);

        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

        /* Prevents the signed in user from appearing in the chat list */
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceChats = FirebaseDatabase.getInstance().getReference().child(NodeNames.CHATS).child(currentUser.getUid());

        /* Data is queried based on the timestamp */
        query = databaseReferenceChats.orderByChild(NodeNames.TIME_STAMP);

        childEventListener = new ChildEventListener() {
            /*
             if there is a new friend added (accepted friend request)
             the new friend will appear without needing to refresh the app
            */
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                updateList(dataSnapshot, true, dataSnapshot.getKey());
            }

            /*
             If the friend sends a request then the last message and the unread count etc
            */
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                updateList(dataSnapshot, false, dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        query.addChildEventListener(childEventListener);

        /* When the query is being sent */
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyChatList.setVisibility(View.VISIBLE);

    }

    /*
     Both onChildAdded and onChildChanged are similar in operation
     so they will use one common method (updateList)
     Used to update the chatList (IsNew indicates wether the user is after
     accepting a friend request which is a new entry (true)
     or the user was already a friend and the data already exists (false)
    */
    private void updateList(DataSnapshot dataSnapshot, boolean isNew, String userId) {
        /* If data is found then the hide the progressbar/emptylist */
        progressBar.setVisibility(View.GONE);
        tvEmptyChatList.setVisibility(View.GONE);

        final String lastMessage, lastMessageTime, unreadCount;

        /*
         If the last message time is not Null, then the last message
         time will be gathered
        */
        if(dataSnapshot.child(NodeNames.LAST_MESSAGE_TIME).getValue()!=null)
            lastMessageTime = dataSnapshot.child(NodeNames.LAST_MESSAGE_TIME).getValue().toString();
        else
            lastMessageTime="";

        /* Gets the unread count */
        unreadCount=dataSnapshot.child(NodeNames.UNREAD_COUNT).getValue()==null?
                "0":dataSnapshot.child(NodeNames.UNREAD_COUNT).getValue().toString();

        /* Used to get the user's username and photo name */
        databaseReferenceUsers.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /* makes sure that the returned username is not null */
                String fullName = dataSnapshot.child(NodeNames.NAME).getValue()!=null?
                        dataSnapshot.child(NodeNames.NAME).getValue().toString():"";
                String photoName  = userId +".jpg";

                /* Adds to the chat list */
                ChatListModel chatListModel = new ChatListModel(userId, fullName, photoName, unreadCount, lastMessageTime);

                if(isNew) {
                    /* Add the new user into the list */
                    chatListModelList.add(chatListModel);
                    userIds.add(userId);
                }
                else {
                    /*
                     if it is not a new user, the users index will be found
                     list will be updated at that index
                    */
                    int indexOfClickedUser = userIds.indexOf(userId) ;
                    chatListModelList.set(indexOfClickedUser, chatListModel);
                }

                chatListAdapter.notifyDataSetChanged();

            }

            /* If the chat list message can not be fetched */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.failed_to_fetch_chat_list, databaseError.getMessage())
                        , Toast.LENGTH_SHORT).show();

            }
        });

    }

    /* To get the child to stop listening to the event */
    @Override
    public void onDestroy() {
        super.onDestroy();
        query.removeEventListener(childEventListener);
    }
}
