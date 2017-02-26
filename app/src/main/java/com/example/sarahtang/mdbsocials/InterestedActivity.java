package com.example.sarahtang.mdbsocials;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.sarahtang.mdbsocials.NewSocialActivity.ref;

public class InterestedActivity extends AppCompatActivity {
    //This activity provides the list of the people who are interested in the activity, if those people exist

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested);

        RecyclerView recyclerViewInterested = (RecyclerView) findViewById(R.id.recyclerInterested);
        recyclerViewInterested.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ArrayList<String> peopleInterested = getIntent().getStringArrayListExtra("peopleList");

        //ArrayList<String> peopleInterested = ref.child("socials").child("peopleInterested").getValue(ArrayList.class);

        InterestedAdapter interestedAdapter = new InterestedAdapter(getApplicationContext(), peopleInterested);
        recyclerViewInterested.setAdapter(interestedAdapter);
        }
    }

