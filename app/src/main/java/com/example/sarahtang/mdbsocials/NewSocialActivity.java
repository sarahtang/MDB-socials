package com.example.sarahtang.mdbsocials;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

/**
 * New Social Activity = Place to put in required information to create a new social
 */

public class NewSocialActivity extends AppCompatActivity {
    public static DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/social");
    Intent data;
    private static final int PROGRESS = 0x1;
    ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    ObjectAnimator animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_social);

        final EditText editName = (EditText) findViewById(R.id.editName);
        final EditText editDate = (EditText) findViewById(R.id.editDate);
        final EditText editDescription = (EditText) findViewById(R.id.editDescription);
        Button addImageButton = (Button) findViewById(R.id.addImageButton);
        Button buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        final int CAMERA_CODE = 1;

        /**
         * Get photo from gallery or camera
         */
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewSocialActivity.this);
                builder.setMessage(R.string.galleryOrCamera)
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_CODE);
                            }
                        })
                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, 2);
                            }
                        });
                builder.show();
            }
        });

        //Create all the aspects of connection to storage - so that when make new social it gets stored (submit)
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.INVISIBLE);

//                //p.setProgress(50);
//                if(arg0%2==1){
//                    item.setBackgroundColor(Color.rgb(252, 234, 238));
//                }
//                return view;


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (progressStatus < 100) {
                            // Update the progress status
                            progressStatus += 1;

                            // Try to sleep the thread for 20 milliseconds
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // Update the progress bar
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(progressStatus);
                                    // Show the progress on TextView
                                    //tv.setText(progressStatus + "");
                                    progressBar.getProgressDrawable().setColorFilter(Color.rgb(220, 41, 82), PorterDuff.Mode.SRC_IN);
                                    animation = ObjectAnimator.ofInt(progressBar, "progress", 16);
                                    animation.setDuration(500); // 0.5 second
                                    animation.setInterpolator(new DecelerateInterpolator());
                                    animation.start();
                                }
                            });
                        }
                    }
                });


                if (data == null) {
                    Toast.makeText(NewSocialActivity.this, R.string.missingInfo, Toast.LENGTH_SHORT).show();
                } else {

                    Log.d("Got to here", "and here");


                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    final String key = ref.child("socials").push().getKey();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.storageURL));
                    StorageReference riversRef = storageRef.child(key + ".png");
                    riversRef.putFile(data.getData()).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d("NewSocialActivity", "Failure");
                            Toast.makeText(NewSocialActivity.this, "Need an image!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.VISIBLE);

                            Log.d("NewSocialActivity", "CreationSuccess");
                            String name = editName.getText().toString();
                            String date = editDate.getText().toString();
                            String description = editDescription.getText().toString();
                            ArrayList<String> peopleInterested = new ArrayList<>();
                            String host = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                            Social social = new Social(name, date, description, host, 0, peopleInterested, key);
                            ref.child("socials").child(key).setValue(social);


                            //New Intent to go back to List Activity
                            Intent intent = new Intent(NewSocialActivity.this, ListActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
            //make visible PROGRESS BAR(spinning bar), when finishes ==> goes to next view
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.data = data;
    }
}

