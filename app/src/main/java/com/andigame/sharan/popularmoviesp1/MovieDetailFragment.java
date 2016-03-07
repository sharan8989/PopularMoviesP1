package com.andigame.sharan.popularmoviesp1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment for handling movie details.
 */
public class MovieDetailFragment extends Fragment {

    private final String SEPERATOR = " ::: ";
    private String movieData;
    private String NO_OVERVIEW = "There is no overview available for this movie.";

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.detail_content, container, false);

        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            movieData = intent.getStringExtra(Intent.EXTRA_TEXT);

            String[] movieDataArray = movieData.split(SEPERATOR);
            String title = movieDataArray[0];
            String poster = movieDataArray[1];
            String releaseDate = movieDataArray[2];
            String userRating = movieDataArray[3];
            String overview;

            // Check if movie overview is available
            if(movieDataArray.length > 4) {
                overview = movieDataArray[4];
            } else {
                overview = NO_OVERVIEW;
            }

            ((TextView) rootView.findViewById(R.id.movie_title)).setText(title);
            ((TextView) rootView.findViewById(R.id.movie_overview)).setText(overview);
            ((TextView) rootView.findViewById(R.id.movie_release_date)).setText(releaseDate);
            ((TextView) rootView.findViewById(R.id.movie_rating)).setText(userRating);


            Picasso
                    .with(getContext())
                    .load(poster)
                    .into((ImageView) rootView.findViewById(R.id.movie_poster));

        }

        return rootView;
    }
}