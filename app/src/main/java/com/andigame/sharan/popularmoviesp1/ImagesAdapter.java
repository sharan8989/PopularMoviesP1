package com.andigame.sharan.popularmoviesp1;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagesAdapter extends ArrayAdapter {

    final String SEPARATOR = " ::: ";

    public ImagesAdapter(Activity context, List<String> movieInfo) {
        super(context, 0, movieInfo);
    }

    @Override
    public View getView(int position, View changeView, ViewGroup parent) {

        String movieInfo = getItem(position).toString();

        if (changeView == null) {
            changeView = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.grid_movies, parent, false);
        }

        String imageUrl = movieInfo.split(SEPARATOR)[1];

        Picasso
                .with(getContext())
                .load(imageUrl)
                .into((ImageView) changeView);

        return changeView;
    }
}
