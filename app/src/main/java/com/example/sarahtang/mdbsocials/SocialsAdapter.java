package com.example.sarahtang.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by sarahtang on 2/21/17.
 */

public class SocialsAdapter extends RecyclerView.Adapter<SocialsAdapter.CustomViewHolder> {
    private Context context;
    public View view;
    ArrayList<Social> listSocials;

    public SocialsAdapter(Context context, ArrayList<Social> listSocials) {
        this.context = context;
        this.listSocials = listSocials;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.socials_row_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        //CardView cardView;
        final Social social = listSocials.get(listSocials.size()-position-1);
        holder.nameEvent.setText(social.name);
        holder.creatorEvent.setText(social.host);

        if (social.numberInterested == 1) {
            holder.numberInterestedEvent.setText("" + social.numberInterested + " person interested.");
        }
        else {
            holder.numberInterestedEvent.setText("" + social.numberInterested + " people interested.");
        }

        class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
            protected Bitmap doInBackground(String... strings) {
                try {return Glide.
                        with(context).
                        load(strings[0]).
                        asBitmap().
                        into(100, 100). // Width and height
                        get();}
                catch (Exception e) {return null;}
            }

            protected void onProgressUpdate(Void... progress) {}

            protected void onPostExecute(Bitmap result) {
                holder.imageEvent.setImageBitmap(result);
            }
        }

        //FirebaseOptions opts = FirebaseApp.getInstance().getOptions();
        //Log.i(TAG, "Bucket = " + opts.getStorageBucket());

        FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdbsocials-fdfae.appspot.com").child(social.firebaseimageURL + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

    }

    @Override
    public int getItemCount() {
        if (listSocials != null) {
            return listSocials.size();
        }
        else {return 0;}
    }


    //CARD View for custom view holder in List activity recycler view (maybe)
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView nameEvent;
        TextView creatorEvent;
        TextView numberInterestedEvent;
        ImageView imageEvent;
        //CardView cardView;

        public CustomViewHolder(View view) {
            super(view);
            this.nameEvent = (TextView) (view.findViewById(R.id.nameEvent));
            this.creatorEvent = (TextView) (view.findViewById(R.id.creatorEvent));
            this.numberInterestedEvent = (TextView) (view.findViewById(R.id.numberInterestedEvent));
            this.imageEvent = (ImageView) (view.findViewById(R.id.imageEvent));
            CardView cardView = (CardView) (view.findViewById(R.id.viewSocial));


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    context.startActivity(intent);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Social social = listSocials.get((listSocials.size()) - getAdapterPosition() - 1);
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("name", social.name);
                    intent.putExtra("host", social.host);
                    intent.putExtra("description", social.description);
                    intent.putExtra("peopleInterested", social.peopleInterested);
                    intent.putExtra("numberInterested", social.numberInterested);
                    intent.putExtra("firebaseURL", social.firebaseimageURL);
                    intent.putExtra("firebaseKey", ListActivity.keyList.get((listSocials.size()) - getAdapterPosition() - 1));
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

        }

    }

}
