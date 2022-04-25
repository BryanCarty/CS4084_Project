package com.example.chirp.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.chirp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * The Util class contains various static utility functions.
 */
public class Util {
    /**
     * Checks to see if the currently active default data network
     * is available
     * @param context
     * @return
     */
    public static boolean connectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null) {
            return connectivityManager.getActiveNetworkInfo().isAvailable();
        } else {
            return false;
        }
    }

    /* Used to update the unread count for messages */
    public static void updateChatDetails(final Context context, final String currentUserId, final String chatUserId, String lastMessage) {
        /* Used to access firebase as well as the chat of the other user not the current user */
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference chatRef = rootRef.child(NodeNames.CHATS).child(chatUserId).child(currentUserId);

        /* Read the unread count and then increment */
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentCount = "0";
                if (dataSnapshot.child(NodeNames.UNREAD_COUNT).getValue() != null)
                    currentCount = dataSnapshot.child(NodeNames.UNREAD_COUNT).getValue().toString();

                Map chatMap = new HashMap();
                chatMap.put(NodeNames.TIME_STAMP, ServerValue.TIMESTAMP);
                /* Increment unread count by one with each message received */
                chatMap.put(NodeNames.UNREAD_COUNT, Integer.valueOf(currentCount) + 1);
                chatMap.put(NodeNames.LAST_MESSAGE, lastMessage);
                chatMap.put(NodeNames.LAST_MESSAGE_TIME, ServerValue.TIMESTAMP);

                Map chatUserMap = new HashMap();
                chatUserMap.put(NodeNames.CHATS + "/" + chatUserId + "/" + currentUserId, chatMap);

                rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        /* If there is an error in the database */
                        if (databaseError != null)
                            Toast.makeText(context, context.getString(R.string.something_went_wrong, databaseError.getMessage())
                                    , Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, context.getString(R.string.something_went_wrong, databaseError.getMessage())
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* Used to convert long time variable within firebase into a legible time format */
    public static String getTimeAgo(long time) {
        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        final int DAY_MILLIS = 24 * HOUR_MILLIS;

        /* Convert time into miliseconds */
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        /* Handle other possibilities */
        if (time > now || time <= 0) {
            return "";
        }

        /*
         difference between current time and time of
         message in milli seconds
        */
        final long diff = now - time;

        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 59 * MINUTE_MILLIS) {
            /* returns the exact number of minutes */
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            /* returns the exact number of hours */
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            /* returns the exact number of days */
            return diff / DAY_MILLIS + " days ago";
        }
    }
}
