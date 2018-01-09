package com.example.ngockhanh.chatapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ImageView mProfileImage;

    private TextView mProfileName, mProfileStatus, mProfileTotalFriends;
    private Button mProfileSendReqBtn;
    private Button mProfileDeliceBtn;
    private DatabaseReference mUsersDatabase;
    private ProgressDialog mProgress;
    private String mCurrent_state;
    private DatabaseReference mFriendsReqDatabase;
    private DatabaseReference mFriendsDatabase;
    private FirebaseUser mCurrent_User;
    private DatabaseReference mNotificationDatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("user_id");
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);
        mProfileTotalFriends = (TextView) findViewById(R.id.profile_totalFriends);
        mProfileSendReqBtn = (Button) findViewById(R.id.profile_send_req_btn);
        mProfileDeliceBtn=(Button) findViewById(R.id.profile_deline_req_btn);
        mCurrent_User = FirebaseAuth.getInstance().getCurrentUser();

        //
        mCurrent_state = "not_friends";
        mProfileDeliceBtn.setVisibility(View.INVISIBLE);
        mProfileDeliceBtn.setEnabled(false);
        //
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Loading User Data");
        mProgress.setMessage("Please wait while we load user data");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        //Root Ref
        mRootRef=FirebaseDatabase.getInstance().getReference();
        //Database User
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        //Database Friend Request
        mFriendsReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        //Database Friends

        mFriendsDatabase= FirebaseDatabase.getInstance().getReference().child("Friends");
        //Database Notification
        mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("Notifications");
        mAuth = FirebaseAuth.getInstance();
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this)
                        .load(image)
                        .placeholder(R.drawable.man2)
                        .error(R.drawable.man2)
                        .into(mProfileImage);
                //-- ----------FRIENDS LIST/REQUEST FEATURE
                mFriendsReqDatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String req_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("received")){
                                mCurrent_state = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");
                                mProfileDeliceBtn.setVisibility(View.VISIBLE);
                                mProfileDeliceBtn.setEnabled(true);
                            }
                            else {
                                mCurrent_state="req_sent";
                                mProfileSendReqBtn.setText("Cancel Request Friend");
                                mProfileDeliceBtn.setVisibility(View.INVISIBLE);
                                mProfileDeliceBtn.setEnabled(false);
                            }
                        }
                        else {
                            //IF WAS FRIENDS
                            mFriendsDatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //IF was friends, set button is "unfriends"
                                    if(dataSnapshot.hasChild(user_id)){
                                        mCurrent_state = "friends";
                                        mProfileSendReqBtn.setText("Unfriend This Person");
                                        mProfileDeliceBtn.setVisibility(View.INVISIBLE);
                                        mProfileDeliceBtn.setEnabled(false);
                                    }
                                    mProgress.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgress.dismiss();
                                }
                            });
                        }
                        mProgress.dismiss();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do not reaction with button req when send req to network
                mProfileSendReqBtn.setEnabled(false);


                // - --------------------- NOT FRIENDS STATE --------------- ---
                if (mCurrent_state.equals("not_friends")) {
                    DatabaseReference newNotificationref=mRootRef.child("Notifications").child(user_id).push();
                    String newNotificationId=newNotificationref.getKey();

                    HashMap<String,String> notificationData=new HashMap<>();
                    notificationData.put("from",mCurrent_User.getUid());
                    notificationData.put("type","request");

                    Map requestMap=new HashMap();
                    requestMap.put("Friend_req/"+mCurrent_User.getUid()+"/"+user_id+"/request_type","sent");
                    requestMap.put("Friend_req/"+user_id+"/"+mCurrent_User.getUid()+"/request_type","received");
                    requestMap.put("Notifications/"+user_id+"/"+newNotificationId,notificationData);
                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError!=null){
                                Toast.makeText(ProfileActivity.this,"There was some error in sending request",Toast.LENGTH_LONG).show();
                            }
                            mCurrent_state = "req_sent";
                            mProfileSendReqBtn.setText("Cancel Friend Request");
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });

                }
                // -- ------------------------CANCEL REQUEST STATE ----------------------- ----
                if(mCurrent_state.equals("req_sent")){
                    mFriendsReqDatabase.child(mCurrent_User.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsReqDatabase.child(user_id).child(mCurrent_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    mProfileSendReqBtn.setText("Send Friend Request");

                                    mProfileDeliceBtn.setVisibility(View.INVISIBLE);
                                    mProfileDeliceBtn.setEnabled(false);
                                }
                            });

                        }
                    });
                }
                // ----------------------------REQ RECEIVED STATE-------------
                if(mCurrent_state.equals("req_received")){
                    final String mCurrentDate= DateFormat.getDateInstance().format(new Date());
                    Map friendsMap=new HashMap();
                    friendsMap.put("Friends/"+mCurrent_User.getUid()+"/"+user_id+"/date",mCurrentDate);
                    friendsMap.put("Friends/"+user_id+"/"+mCurrent_User.getUid()+"/date",mCurrentDate);

                    //remove from friend_req
                    friendsMap.put("Friend_req/"+mCurrent_User.getUid()+"/"+user_id,null);
                    friendsMap.put("Friend_req/"+user_id+"/"+mCurrent_User.getUid(),null);
                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError==null){
                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state = "friends";
                                mProfileSendReqBtn.setText("Unfriend this person");
                                mProfileDeliceBtn.setVisibility(View.INVISIBLE);
                                mProfileDeliceBtn.setEnabled(false);
                            }
                            else {
                                Toast.makeText(ProfileActivity.this,"There was some error in sending request",Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }

                //------------------------------- UNFRIENDS ---------------
                if(mCurrent_state.equals("friends")){
                    Map unfriendMap=new HashMap();
                    unfriendMap.put("Friends/"+mCurrent_User.getUid()+"/"+user_id,null);
                    unfriendMap.put("Friends/"+user_id+"/"+mCurrent_User.getUid(),null);
                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError==null){
                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state="not_friends";
                                mProfileSendReqBtn.setText("Send Friend Request");
                            }else {
                                Toast.makeText(ProfileActivity.this,"There was some error in sending request",Toast.LENGTH_LONG).show();
                            }

                            mProfileSendReqBtn.setEnabled(true);

                        }
                    });

                }

            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        mUserRef.child("online").setValue(true);
    }
    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }
}
