package com.example.chirp.chats;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.chirp.Common.Constants;
import com.example.chirp.Common.Extras;
import com.example.chirp.Common.NodeNames;
import com.example.chirp.Common.Util;
import com.example.chirp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* onClickListener will handle buttons presses/clicks */

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivSend, ivProfile;
    private TextView tvUserName;
    private EditText etMessage;
    private DatabaseReference mRootRef;
    private FirebaseAuth firebaseAuth;
    private String currentUserId, chatUserId;

    /* Initialises views for message view */
    private RecyclerView rvMessages;
    private SwipeRefreshLayout srlMessages;
    private MessagesAdapter messagesAdapter;
    private List<MessageModel> messagesList;

    /* Only allow 30 messages a page, shows more when they swipe down */
    private int currentPage = 1;
    private static final int RECORD_PER_PAGE = 30;

    private DatabaseReference databaseReferenceMessages;
    /* Track new messages */
    private ChildEventListener childEventListener;

    private String userName, photoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        /* Create the action bar and check it is not null */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            /* Generating the layout and converting it into a viewgroup */
            ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_action_bar, null);

            /* Creates an arrow to allow us to return to the chat list */
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setElevation(0);

            actionBar.setCustomView(actionBarLayout);
            actionBar.setDisplayOptions(actionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
        }

        ivProfile = findViewById(R.id.ivProfile);
        tvUserName = findViewById(R.id.tvUserName);

        ivSend = findViewById(R.id.ivSend);
        etMessage = findViewById(R.id.etMessage);

        /* Handles the send message button being pressed */
        ivSend.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        /* Checks that the key is passed */
        if(getIntent().hasExtra(Extras.USER_KEY)) {
            chatUserId = getIntent().getStringExtra(Extras.USER_KEY);
            /* Gets profile picture for the action bar */
            photoName = chatUserId + ".jpg";
        }

        if (getIntent().hasExtra(Extras.USER_NAME))
            /* Gets userName for the action bar */
            userName = getIntent().getStringExtra(Extras.USER_NAME);

        tvUserName.setText(userName);

        /*
         Allows user to chat if they have no profile image
         It will show the default image instead of an error
        */
        if(!TextUtils.isEmpty(photoName) && photoName!=null) {
            /* Contains all the profile pictures */
            StorageReference photoRef = FirebaseStorage.getInstance().getReference().child(Constants.IMAGES_FOLDER + "/" + photoName);

            /* Download the image */
            photoRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(ChatActivity.this)
                    .load(uri)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(ivProfile));
        }


        rvMessages = findViewById(R.id.rvMessages);
        srlMessages = findViewById(R.id.srlMessages);

        messagesList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messagesList);

        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(messagesAdapter);

        /* Seperate method to get messages */
        loadMessages();

        /* Update the unread count of the current user to zero (upon reading a message) */
        mRootRef.child(NodeNames.CHATS).child(currentUserId).child(chatUserId).child(NodeNames.UNREAD_COUNT).setValue(0);

        /* Scrolls down to the most recent message */
        rvMessages.scrollToPosition(messagesList.size() - 1);

        /*
         Called when the user swipes on the chat list
         Increments page
        */
        srlMessages.setOnRefreshListener(() -> {
            currentPage++;
            loadMessages();
        });
    }

    private void sendMessage(final String msg, String pushId) {
        try {
            /* used to put the values into the firebase database */
            HashMap messageMap = new HashMap();
            messageMap.put(NodeNames.MESSAGE_ID, pushId);
            messageMap.put(NodeNames.MESSAGE, msg);
            messageMap.put(NodeNames.MESSAGE_FROM, currentUserId);
            messageMap.put(NodeNames.MESSAGE_TIME, ServerValue.TIMESTAMP);

            String currentUserRef = NodeNames.MESSAGES + "/" + currentUserId + "/" + chatUserId;
            String chatUserRef = NodeNames.MESSAGES + "/" + chatUserId + "/" + currentUserId;

            /*
             used to update the messagemap of both the user sending
             and being sent the message
            */
            HashMap messageUserMap = new HashMap();
            messageUserMap.put(currentUserRef + "/" + pushId, messageMap);
            messageUserMap.put(chatUserRef + "/" + pushId, messageMap);

            /* edit text is made empty */
            etMessage.setText("");

            /*
             Will update nodes and children from the root and
             update the hashmap into the database
            */
            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference ref) {
                    /* Deals with there being an issue with the database */
                    if (databaseError != null) {
                        Toast.makeText(ChatActivity.this, getString(R.string.failed_to_send_message, databaseError.getMessage())
                                , Toast.LENGTH_SHORT).show();
                    }
                    {
                        Toast.makeText(ChatActivity.this, R.string.message_sent_successfully, Toast.LENGTH_SHORT).show();
                        String title="";
                        String lastMessage= !title.equals("New Message")?title:msg;
                        Util.updateChatDetails(ChatActivity.this, currentUserId, chatUserId, lastMessage);
                    }
                }
            });
        } catch (Exception ex) {
            /* will catch if the message is unable to be sent */
            Toast.makeText(ChatActivity.this, getString(R.string.failed_to_send_message, ex.getMessage())
                    , Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMessages() {
        /* Clears the current list to start fresh */
        messagesList.clear();
        databaseReferenceMessages = mRootRef.child(NodeNames.MESSAGES).child(currentUserId).child(chatUserId);

        /* Used to limit the messages on the screen, multiplies messages by the page number */
        Query messageQuery = databaseReferenceMessages.limitToLast(currentPage * RECORD_PER_PAGE);

        if (childEventListener != null)
            messageQuery.removeEventListener(childEventListener);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /*
                 Runtime error if there is no empty constructor
                 in the messageModelClass
                */
                MessageModel message = dataSnapshot.getValue(MessageModel.class);

                messagesList.add(message);
                messagesAdapter.notifyDataSetChanged();
                /*
                 Used to automatically scroll to the latest message
                 User will need to scroll up to access previous message
                */
                rvMessages.scrollToPosition(messagesList.size() - 1);
                /* Stops visual appearance of a refresh */
                srlMessages.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                loadMessages();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                srlMessages.setRefreshing(false);
            }
        };

        messageQuery.addChildEventListener(childEventListener);
    }

    @Override
    public void onClick(View view) {
        /* Generates push ID then sends message when send button is pressed*/
        DatabaseReference userMessagePush = mRootRef.child(NodeNames.MESSAGES).child(currentUserId).child(chatUserId).push();
        String pushId = userMessagePush.getKey();
        sendMessage(etMessage.getText().toString().trim(), pushId);
    }

    /* Used to implement functionality of the back arrow on the action bar on the top of the messages list */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /* Reset the unread count to zero when back button is pressed */
    @Override
    public void onBackPressed() {
        mRootRef.child(NodeNames.CHATS).child(currentUserId).child(chatUserId).child(NodeNames.UNREAD_COUNT).setValue(0);
        super.onBackPressed();

    }
}
