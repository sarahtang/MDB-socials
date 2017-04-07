package com.example.sarahtang.mdbsocials;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * List Activity: Main list for social events
 */

public class ListActivity extends AppCompatActivity implements View.OnClickListener {
    //List view = recycler activity and main list showing the list of socials
    protected SocialsAdapter socialsAdapter;
    ArrayList<Social> socials;
    static public ArrayList<String> keyList;

    FloatingActionButton fab;
    FloatingActionButton signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        socials = new ArrayList<>();
        keyList = new ArrayList<>();
        final ArrayList<Social> listSocials = socials;

        //Connect with adapter
        socialsAdapter = new SocialsAdapter(getApplicationContext(), socials);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerSocial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(socialsAdapter);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials");

        /**
         * On Click listener
         */

        fab = (FloatingActionButton) findViewById(R.id.fab);
        signOut = (FloatingActionButton) findViewById(R.id.signOut);
        fab.setOnClickListener(this);
        signOut.setOnClickListener(this);

        //This line orders the socials by time (recent to least recent) in the beginning
        ref.orderByChild("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listSocials.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Social newSocial = snapshot.getValue(Social.class);
                    keyList.add(snapshot.getKey());
                    List<String> peopleInterested = (List<String>) snapshot.child("peopleInterested").getValue();
                    listSocials.add(newSocial);
                }
                if (socialsAdapter != null) {
                    socialsAdapter.notifyDataSetChanged();
                }
                recyclerView.setAdapter(socialsAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * On Click listener implementation
     * @param view
     */

    public void onClick(View view) {
        if (view.getId() == R.id.fab) {
            //Go to new social activity
            Log.d("ListAct", "NewSocial");
            Intent intent = new Intent(getApplicationContext(), NewSocialActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.signOut) {
            //Sign out user and go back to login activity
            Log.d("ListAct", "logout");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

        }
    }

}
