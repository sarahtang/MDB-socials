package com.example.sarahtang.mdbsocials;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by sarahtang on 2/23/17.
 * A model class that contains all properties of a social
 */

public class Social {
    public String name;
    public String date;
    public String description;
    public String host; //email
    public int numberInterested;
    public String firebaseimageURL;
    public ArrayList<String> peopleInterested;

    public Social() {
        Log.e("SocialClass", "noInfo");
    }

    public Social (String name, String date, String description, String creator, int numberInterested, ArrayList<String> peopleInterested, String firebaseimageURL) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.host = creator;
        this.numberInterested = numberInterested;
        this.peopleInterested = peopleInterested;
        this.firebaseimageURL = firebaseimageURL;

        }
}
