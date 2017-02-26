package com.example.sarahtang.mdbsocials;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by sarahtang on 2/23/17.
 */

public class Social {
    String name;
    String date;
    String description;
    String host; //should be their email(?)
    int numberInterested;
    String firebaseimageURL;
    ArrayList<String> peopleInterested;

    public Social() {
        Log.e("whoops", "rip");
    }

    public Social (String name, String date, String description, String creator, int numberInterested, ArrayList<String> peopleInterested, String firebaseimageURL) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.host = creator;
        this.numberInterested = numberInterested;
        this.peopleInterested = peopleInterested;
        this.firebaseimageURL = firebaseimageURL;

        //
        }
}
