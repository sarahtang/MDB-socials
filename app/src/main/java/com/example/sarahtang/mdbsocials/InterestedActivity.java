package com.example.sarahtang.mdbsocials;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

/**
 * Interested activity sets the layout view and connects with Interested Adapter
 * to display the people that are interested.
 * List of emails of people interested in a social.
 */

public class InterestedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested);

        //Connect people interested in social with recycler view layout
        Log.d("InterestedActivity", "Int");
        RecyclerView recyclerViewInterested = (RecyclerView) findViewById(R.id.recyclerInterested);
        recyclerViewInterested.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ArrayList<String> peopleInterested = getIntent().getStringArrayListExtra("peopleList");
        InterestedAdapter interestedAdapter = new InterestedAdapter(this, peopleInterested);
        recyclerViewInterested.setAdapter(interestedAdapter);
        }
    }

