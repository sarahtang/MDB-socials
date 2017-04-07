package com.example.sarahtang.mdbsocials;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sarahtang on 2/21/17.
 *
 * Interested adapter connects with list of people (email)
 * that are interested in the social.
 */


public class InterestedAdapter extends RecyclerView.Adapter<InterestedAdapter.CustomViewHolder> {
    private Context context;
    private ArrayList names;

    public InterestedAdapter(Context context, ArrayList names){
        this.context = context;
        this.names = names;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interested_row_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void  onBindViewHolder(InterestedAdapter.CustomViewHolder holder, int position) {
        holder.textView.setText(names.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            textView = (TextView) (view.findViewById(R.id.textView_interested));
        }
    }
}