package com.example.ngockhanh.chatapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUser;
    private View mainView;



    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mainView = inflater.inflate(R.layout.fragment_friends, container, false);
        mFriendsList = (RecyclerView) mainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUser);
        mFriendsDatabase.keepSynced(true);
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabase.keepSynced(true);


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendViewHolder> friendRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendViewHolder>(
                Friends.class,
                R.layout.users_single_layout,
                FriendViewHolder.class,
                mFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendViewHolder friendViewHolder, Friends friends, int position) {
                friendViewHolder.setDate(friends.getDate());
                final String list_user_id=getRef(position).getKey();
                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String user_name=dataSnapshot.child("name").getValue().toString();
                        String user_thumb=dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")){
                            String user_Online=dataSnapshot.child("online").getValue().toString();
                            friendViewHolder.setOnlineStatus(user_Online);

                        }


                        friendViewHolder.setName(user_name);
                        friendViewHolder.setImage(user_thumb,getContext());

                        friendViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence options[]=new CharSequence[]{"Open Profile","Send Message"};
                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        switch (i){
                                            case 0:
                                                Intent profileIntent=new Intent(getContext(),ProfileActivity.class);
                                                profileIntent.putExtra("user_id",list_user_id);
                                                startActivity(profileIntent);
                                                break;
                                            case 1:
                                                Intent chatIntent=new Intent(getContext(),ChatActivity.class);
                                                chatIntent.putExtra("user_id",list_user_id);
                                                chatIntent.putExtra("user_name",user_name);
                                                startActivity(chatIntent);
                                                break;
                                        }

                                    }
                                });
                                builder.show();

                            }
                        });



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        mFriendsList.setAdapter(friendRecyclerViewAdapter);


    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public FriendViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setDate(String date) {
            //this.date = date;
            TextView dateView=(TextView) mView.findViewById(R.id.users_single_status);
            dateView.setText(date);
        }

        public void setName(String user_name) {
            TextView userNameView = (TextView) mView.findViewById(R.id.users_single_name);
            userNameView.setText(user_name);
        }

        public void setImage(String user_thumb, Context context) {
            CircleImageView userImageView=(CircleImageView)mView.findViewById(R.id.users_single_image);
            Picasso.with(context)
                    .load(user_thumb)
                    .placeholder(R.drawable.test)
                    .error(R.drawable.test)
                    .into(userImageView);
        }
        public void setOnlineStatus(String onlineStatus) {
            ImageView imgOnline=(ImageView)mView.findViewById(R.id.users_single_online_icon);
            if(onlineStatus.equals("true")){
                imgOnline.setVisibility(View.VISIBLE);

            }else {
                imgOnline.setVisibility(View.INVISIBLE);

            }

        }
    }

}
