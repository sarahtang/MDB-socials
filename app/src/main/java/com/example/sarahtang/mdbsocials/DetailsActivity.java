package com.example.sarahtang.mdbsocials;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.example.sarahtang.mdbsocials.NewSocialActivity.ref;

public class DetailsActivity extends AppCompatActivity {
    ImageView imageDetail;
    TextView nameDetail; //title
    TextView interestedDetail;//number of people interested
    TextView hostDetail;
    TextView descriptionDetail;
    String firebaseKey;
    List<String> peopleInterested;
    String currEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        imageDetail = (ImageView) findViewById(R.id.imageDetail);
        nameDetail = (TextView) findViewById(R.id.nameDetail);
        interestedDetail = (TextView) findViewById(R.id.interestedDetail);
        hostDetail = (TextView) findViewById(R.id.hostDetail);
        descriptionDetail = (TextView) findViewById(R.id.descriptionDetail);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String host = intent.getStringExtra("host");
        String description = intent.getStringExtra("description");
        String image = intent.getStringExtra("firebaseURL");
        final int numberInterested = intent.getIntExtra("numberInterested", -1);
        ArrayList<String> resultArray = intent.getStringArrayListExtra("peopleInterested");
        if (resultArray == null) {
            resultArray = new ArrayList<>();
        }
        final ArrayList<String> peopleInterested = resultArray;
        firebaseKey = intent.getStringExtra("firebaseKey");

        class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
            protected Bitmap doInBackground(String... strings) {
                try {return Glide.
                        with(getApplicationContext()).
                        load(strings[0]).
                        asBitmap().
                        into(100, 100). // Width and height
                        get();}
                catch (Exception e) {return null;}
            }

            protected void onProgressUpdate(Void... progress) {}

            protected void onPostExecute(Bitmap result) {
                imageDetail.setImageBitmap(result);
            }
        }


        FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-fdfae.appspot.com").child(image + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                (new DownloadFilesTask()).execute(uri.toString());
                Log.d("ye", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("sad", exception.toString());
            }
        });

        nameDetail.setText(name);
        hostDetail.setText("Hosted by " + host);
        descriptionDetail.setText("Description: " + description);
        if (numberInterested == 1) {
            interestedDetail.setText(numberInterested + " person interested.");
        }
        else {
            interestedDetail.setText(numberInterested + " people interested.");
        }

        FloatingActionButton fabInterested = (FloatingActionButton) findViewById(R.id.fabInterested);
        fabInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentInterested = new Intent(getApplicationContext(), InterestedActivity.class);
                intentInterested.putStringArrayListExtra("peopleList", peopleInterested);
                startActivity(intentInterested);

            }
        });

        final FloatingActionButton fabStar = (FloatingActionButton) findViewById(R.id.fabStar);
        fabStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabStar.setRippleColor(Color.parseColor("#ffcf32"));
                fabStar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffcf32")));
                //String currEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                if (!peopleInterested.contains(currEmail)) {
                    addInterested();
                }
                else {
                    Toast.makeText(getApplicationContext(), "You're already interested in this event!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void addInterested() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials");

        ref.child(firebaseKey).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                //String currEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                int numInterested = mutableData.child("numberInterested").getValue(Integer.class);
                numInterested++;
                /*
                if (numInterested == 1) {
                    interestedDetail.setText(numInterested + " person interested.");
                }
                else {
                    interestedDetail.setText(numInterested + " people interested.");
                }
                */

                DetailsActivity.addInterestedToDatabase(numInterested,firebaseKey);

                ArrayList<String> a = (ArrayList) mutableData.child("peopleInterested").getValue();
                if (a == null) {
                    a = new ArrayList<String>();
                }
                if (!a.contains(currEmail)) {
                    a.add(currEmail);
                    ref.child(firebaseKey).child("peopleInterested").setValue(a);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }



    public static void addInterestedToDatabase(int interested, String firekey) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials");
        ref.child(firekey).child("numberInterested").setValue(interested);

    }
}
