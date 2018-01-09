package com.example.ngockhanh.chatapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ngoc Khanh on 1/9/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<Messages> mMessageList;

    public MessageAdapter(List<Messages> messagesList) {
        this.mMessageList=messagesList;
    }

    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageAdapter.MessageViewHolder messageViewHolder, int position) {
        Messages message=mMessageList.get(position);
        messageViewHolder.messageText.setText(message.getMessage());
        //  messageViewHolder.profileImage
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public  class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public CircleImageView profileImage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText=(TextView)itemView.findViewById(R.id.message_text_layout);
            profileImage=(CircleImageView)itemView.findViewById(R.id.message_profile_layout);

        }
    }
}
