package com.example.ngockhanh.chatapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Ngoc Khanh on 1/9/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;
    private Context context;
    private DatabaseReference mUserRef;
    private String mChatUser;

    public MessageAdapter(List<Messages> messagesList, Context ctx, String chat_User) {

        this.mMessageList = messagesList;
        this.context = ctx;
        this.mChatUser = chat_User;
    }

    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(v);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(final MessageAdapter.MessageViewHolder messageViewHolder, int position) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        final Messages message = mMessageList.get(position);
        String from_user = message.getFrom();
        String messageType = message.getType();

////-----------------------CURRENT USER------------------
        if (from_user.equals(current_user_id)) {
            mUsersDatabase.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    RelativeLayout.LayoutParams params =
//                            new RelativeLayout.LayoutParams(64,
//                                    64);
//                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//                    params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//                    messageViewHolder.profileImage.setLayoutParams(params);

                    ///
                    String nameDisplay = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    messageViewHolder.nameText.setText(nameDisplay);
                    Picasso.with(context)
                            .load(image)
                            .placeholder(R.drawable.man2)
                            .error(R.drawable.man2)
                            .into(messageViewHolder.profileImage);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if (messageType.equals("text")) {
                messageViewHolder.messageText.setVisibility(View.VISIBLE);
                messageViewHolder.messageImage.setVisibility(View.GONE);
                messageViewHolder.messageText.setText(message.getMessage());
            } else {

                messageViewHolder.messageText.setVisibility(View.GONE);
                messageViewHolder.messageImage.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(message.getMessage())
                        .placeholder(R.drawable.man2)
                        .error(R.drawable.man2)
                        .into(messageViewHolder.messageImage);
            }

        }
        ///-------------------- CHAT USER--------------------
        else {
            mUsersDatabase.child(mChatUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String nameDisplay = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    messageViewHolder.nameText.setText(nameDisplay);
                    Picasso.with(context)
                            .load(image)
                            .placeholder(R.drawable.man2)
                            .error(R.drawable.man2)
                            .into(messageViewHolder.profileImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (messageType.equals("text")) {
                messageViewHolder.messageText.setVisibility(View.VISIBLE);
                messageViewHolder.messageImage.setVisibility(View.GONE);
                messageViewHolder.messageText.setText(message.getMessage());
            } else {

                messageViewHolder.messageText.setVisibility(View.GONE);
                messageViewHolder.messageImage.setVisibility(View.VISIBLE);

                Picasso.with(context)
                        .load(message.getMessage())
                        .placeholder(R.drawable.man2)
                        .error(R.drawable.man2)
                        .into(messageViewHolder.messageImage);
            }
        }

        //  messageViewHolder.profileImage
    }

    public void setVisibleType(String type) {
        if (type.equals("text")) {

        } else if (type.equals("image")) {

        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public CircleImageView profileImage;
        public TextView nameText;
        public TextView timeText;
        public ImageView messageImage;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.message_text_layout);
            nameText = (TextView) itemView.findViewById(R.id.name_text_layout);
            timeText = (TextView) itemView.findViewById(R.id.time_text_layout);
            profileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_layout);
            messageImage = (ImageView) itemView.findViewById(R.id.message_image_layout);

        }
    }
}
