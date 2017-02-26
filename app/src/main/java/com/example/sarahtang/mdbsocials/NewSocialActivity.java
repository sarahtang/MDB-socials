package com.example.sarahtang.mdbsocials;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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

import static android.R.attr.button;
import static android.R.attr.data;
import static android.R.attr.key;

//Create a new social; need to have an input of where to put in required information and such

public class NewSocialActivity extends AppCompatActivity{
    public static DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/social");
    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_social);

        final EditText editName = (EditText) findViewById(R.id.editName);
        final EditText editDate = (EditText) findViewById(R.id.editDate);
        final EditText editDescription = (EditText) findViewById(R.id.editDescription);
        Button addImageButton = (Button) findViewById(R.id.addImageButton);
        Button buttonSubmit = (Button) findViewById(R.id.buttonSubmit);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewSocialActivity.this);
                builder.setMessage("Would you like to upload photo from gallery or take a photo with camera?")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, 1);
                            }
                        })
                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, 2);
                        }});

                builder.show();

            }
        });

        //Create all the aspects of connection to storage - so that when make new social it gets stored

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                final String key = ref.child("socials").push().getKey();
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-fdfae.appspot.com");
                StorageReference riversRef = storageRef.child(key + ".png");

                riversRef.putFile(data.getData()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(NewSocialActivity.this, "Need an image!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String name = editName.getText().toString();
                        String date = editDate.getText().toString();
                        String description = editDescription.getText().toString();
                        ArrayList<String> peopleInterested = new ArrayList<>();
                        String host = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                        Social social = new Social(name, date, description, host, 0, peopleInterested, key);
                        ref.child("socials").child(key).setValue(social);

                        Intent intent = new Intent(NewSocialActivity.this, ListActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.data = data;
    }
}

