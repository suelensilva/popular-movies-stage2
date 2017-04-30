package com.sooba.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sooba.popularmovies.model.Trailer;

import java.util.ArrayList;
import java.util.List;

class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {

    private List<Trailer> trailers = new ArrayList<>();

    TrailerAdapter() {
    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {
        Trailer trailer = trailers.get(position);

        holder.trailerNameTextView.setText(trailer.getName());
        holder.trailerLayoutItem.setTag(trailer);
    }

    public void setData(List<Trailer> trailers) {
        this.trailers.clear();

        if(trailers != null) {
            this.trailers.addAll(trailers);
        }
    }

    @Override
    public int getItemCount() {
        if(trailers == null) return 0;
        return trailers.size();
    }

    class TrailerHolder extends RecyclerView.ViewHolder {

        final View trailerLayoutItem;
        final TextView trailerNameTextView;

        TrailerHolder(View itemView) {
            super(itemView);

            trailerLayoutItem = itemView;
            trailerNameTextView = (TextView) itemView.findViewById(R.id.tv_trailer_name);
        }
    }
}
