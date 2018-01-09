package com.example.ngockhanh.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    //Android Layout
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;
    private Button mStatusBtn;
    private Button mImageBtn;
    private final int PICK_IMAGE = 1;
    private ProgressDialog mProgress;

    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;


    //Firebase Storage
    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mAuth = FirebaseAuth.getInstance();
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        //Init
        mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        mName = (TextView) findViewById(R.id.settings_display_name);
        mStatus = (TextView) findViewById(R.id.settings_status);
        mStatusBtn = (Button) findViewById(R.id.settings_change_status_btn);
        mImageBtn = (Button) findViewById(R.id.settings_change_image_btn);
        //Firebase Storage
        mImageStorage = FirebaseStorage.getInstance().getReference();
        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        //keep session and don't reload data from network
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                mName.setText(name);
                mStatus.setText(status);
                Picasso.with(SettingActivity.this)
                       .load(image)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.test)
                        .error(R.drawable.test)
                        .into(mDisplayImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(SettingActivity.this)
                                        .load(image)
                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                        .placeholder(R.drawable.test)
                                        .error(R.drawable.test)
                                        .into(mDisplayImage);
                            }
                        });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Add Listener
        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status_value = mStatus.getText().toString();
                Intent settingIntent = new Intent(SettingActivity.this, StatusActivity.class);
                settingIntent.putExtra("current_status", status_value);
                startActivity(settingIntent);
            }
        });
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galaryIntent = new Intent();
                galaryIntent.setType("image/*");
                galaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galaryIntent, "SELECT IMAGE"), PICK_IMAGE);

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            Log.d("Setting Activity", "onStop: in Setting");
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Setting Activity", "onStart: in Setting");
        mUserRef.child("online").setValue(true);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setAspectRatio(1, 1)
                    .setMinCropWindowSize(500,500)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgress=new ProgressDialog(SettingActivity.this);
                mProgress.setTitle("Change profile Image");
                mProgress.setMessage("Please wait while we upload and process the image...");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                Uri resultUri = result.getUri();
                final File thumb_filePath=new File(resultUri.getPath());
                String current_uid = mCurrentUser.getUid();
                try {
                    Bitmap thumb_bitmap=new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_bytes = baos.toByteArray();
                    final StorageReference thumb_filepath=mImageStorage.child("profile_images").child("thumbs").child(current_uid+".jpg");
                    StorageReference filePath = mImageStorage.child("profile_images").child(current_uid + ".jpg");
                    filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                final String image_download_url=task.getResult().getDownloadUrl().toString();
                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_bytes);
                                //upload Thumbs image
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                        String thumb_downloadUrl=thumb_task.getResult().getDownloadUrl().toString();
                                        if(thumb_task.isSuccessful()){
                                            Map update_hashMap=new HashMap<>();
                                            update_hashMap.put("image",image_download_url);
                                            update_hashMap.put("thumb_image",thumb_downloadUrl);
                                            //set Value to image profile
                                            mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        mProgress.dismiss();
                                                        Toast.makeText(SettingActivity.this,"Upload Completely",Toast.LENGTH_SHORT).show();
                                                        Picasso.with(SettingActivity.this).load(image_download_url).into(mDisplayImage);
                                                    }
                                                    else {
                                                        Toast.makeText(SettingActivity.this,"Error in Uploading Thumnail",Toast.LENGTH_SHORT).show();
                                                        mProgress.dismiss();
                                                    }

                                                }
                                            });
                                        }
                                    }
                                });




                            } else {
                                Toast.makeText(SettingActivity.this,"Error in Uploading",Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



}
