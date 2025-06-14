// File: app/src/main/java/com/example/womenapp/ChatAdapter.java
package com.example.womenapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    // These constants help distinguish between user and bot messages
    // so the adapter knows which layout to inflate (item_message_user or item_message_bot)
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    private List<Message> messageList; // The list of chat messages to display

    // Constructor: Takes the list of messages
    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    // Determines which layout (user or bot) to use for a given message
    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).isUser()) {
            return VIEW_TYPE_USER; // It's a user message
        } else {
            return VIEW_TYPE_BOT; // It's a bot message
        }
    }

    // Called when RecyclerView needs a new ViewHolder to represent an item.
    // This is where you inflate your individual message layout (item_message_user or item_message_bot).
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_USER) {
            // Inflate layout for user messages (aligned right, blue bubble)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_user, parent, false);
        } else {
            // Inflate layout for bot messages (aligned left, gray bubble)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_bot, parent, false);
        }
        return new MessageViewHolder(view);
    }

    // Called by RecyclerView to display the data at the specified position.
    // This is where you put the message text into the TextView of the ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position); // Get the current message object
        holder.messageTextView.setText(message.getText()); // Set its text to the TextView
    }

    // Returns the total number of items in the data set held by the adapter.
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder class: Holds references to the views for each item (in this case, just a TextView)
    // This improves performance by avoiding repeated findViewById() calls.
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView; // The TextView inside your message bubble layout

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link the TextView in the layout XML to this Java variable
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
