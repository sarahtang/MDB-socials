package com.example.sarahtang.mdbsocials;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.sarahtang.mdbsocials.NewSocialActivity.ref;

public class ListActivity extends AppCompatActivity {
    //List view = recycler activity and main list showing the list of socials
    protected SocialsAdapter socialsAdapter;
    ArrayList<Social> socials;
    static public ArrayList<String> keyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        socials = new ArrayList<>();
        keyList = new ArrayList<>();
        final ArrayList<Social> listSocials = socials;
        socialsAdapter = new SocialsAdapter(getApplicationContext(), socials);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerSocial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //change this to getApplicationContext()
        recyclerView.setAdapter(socialsAdapter);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/socials");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewSocialActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton signOut = (FloatingActionButton) findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

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

}
