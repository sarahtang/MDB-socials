package com.example.sarahtang.mdbsocials;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 * Details Activity: Detailed information about each social
 */

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageDetail;
    TextView nameDetail; //title
    TextView interestedDetail;//number of people interested
    TextView hostDetail;
    TextView descriptionDetail;
    String firebaseKey;
    ArrayList<String> peopleInterested;
    String currEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    boolean buttonPressed = false;
    FloatingActionButton fabInterested;
    FloatingActionButton fabStar;
    final int DEFAULT_INTERESTED = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        /**
         * On Click Listener implementation in activity
         */

        fabInterested = (FloatingActionButton) findViewById(R.id.fabInterested);
        fabInterested.setOnClickListener(this);
        fabStar = (FloatingActionButton) findViewById(R.id.fabStar);
        fabStar.setOnClickListener(this);
        imageDetail = (ImageView) findViewById(R.id.imageDetail);
        nameDetail = (TextView) findViewById(R.id.nameDetail);
        interestedDetail = (TextView) findViewById(R.id.interestedDetail);
        hostDetail = (TextView) findViewById(R.id.hostDetail);
        descriptionDetail = (TextView) findViewById(R.id.descriptionDetail);

        //Getting information from intent from socials adapter
        Intent intent = getIntent();
        String name = intent.getStringExtra(Utils.NAME_KEY);
        String host = intent.getStringExtra(Utils.HOST_KEY);
        String description = intent.getStringExtra(Utils.DESCRIPTION_KEY);
        String image = intent.getStringExtra(Utils.FIREBASE_URL);
        final int numberInterested = intent.getIntExtra(Utils.NUMBER_INTERESTED, DEFAULT_INTERESTED);
        ArrayList<String> resultArray = intent.getStringArrayListExtra(Utils.PEOPLE_INTERESTED);
        if (resultArray == null) {
            resultArray = new ArrayList<>();
        }
        peopleInterested = resultArray;
        firebaseKey = intent.getStringExtra(Utils.FIREBASE_KEY);

        //Retrieving image from firebase storage
        final String url = "gs://mdbsocials-fdfae.appspot.com";
        FirebaseStorage.getInstance().getReferenceFromUrl(url).child(image + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                (new DownloadFilesTask()).execute(uri.toString());
                Log.d("GotImageFromStorage", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("NoImageFromStorage", exception.toString());
            }
        });

        //Setting text for each Text view in layout
        nameDetail.setText(name);
        hostDetail.setText(getString(R.string.hostedBy) + host);
        descriptionDetail.setText(getString(R.string.descriptionIntro) + description);

        switch(numberInterested) {
            case 1:
                interestedDetail.setText(numberInterested + getString(R.string.personInterested));
                break;
            default:
                interestedDetail.setText(numberInterested + getString(R.string.peopleInterested));
        }
    }

    //Download image asynchronously
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

    /**
     * Transaction to increase number of people interested by one and to add person to
     * interested list (if not already added);
     * Modularized ish
     */
    public void addInterested() {
        Log.d("TRANSACTION", "Success");

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials");
        ref.child(firebaseKey).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                //String currEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                if (!buttonPressed) {
                    buttonPressed = true;
                    int numInterested = mutableData.child("numberInterested").getValue(Integer.class);
                    numInterested++;
                    Log.d("Details", "transaction");
                    DetailsActivity.addInterestedToDatabase(numInterested,firebaseKey);


                    ArrayList<String> a = (ArrayList) mutableData.child("peopleInterested").getValue();
                    if (a == null) {
                        a = new ArrayList<String>();
                    }

                    if (!a.contains(currEmail)) {
                        a.add(currEmail);
                        ref.child(firebaseKey).child("peopleInterested").setValue(a);
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.toastAlreadyInterested, Toast.LENGTH_SHORT).show();
                }

                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    public static void addInterestedToDatabase(int interested, String firekey) {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials");
        ref.child(firekey).child("numberInterested").setValue(interested);
    }

    /**
     * All of the on click listeners
     * @param view
     */

    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.fabInterested:
                Log.d("Details", "listInterested");
                Intent intentInterested = new Intent(getApplicationContext(), InterestedActivity.class);
                intentInterested.putStringArrayListExtra("peopleList", peopleInterested);
                startActivity(intentInterested);
                break;
            case R.id.fabStar:
                /**
                 * Interested button
                 */
                Log.d("Details", "interested");
                fabStar.setRippleColor(Color.parseColor("#ffcf32"));
                fabStar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffcf32")));

                if (!peopleInterested.contains(currEmail)) {
                    Toast.makeText(getApplicationContext(), "Interested In Event!", Toast.LENGTH_SHORT).show();

                    addInterested();
                    //TODO FIX
                    //interestedDetail.setText(numberInterested);
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.toastAlreadyInterested, Toast.LENGTH_SHORT).show();
                }
        }



    }
}
