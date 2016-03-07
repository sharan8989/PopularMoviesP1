package com.andigame.sharan.popularmoviesp1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment {

    private static ArrayList<String> moviesInfo = new ArrayList<>();

    private ImagesAdapter imagesAdapter;

    public MovieListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        imagesAdapter = new ImagesAdapter(getActivity(), moviesInfo);

        View rootView = inflater.inflate(R.layout.main_fragment, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movies_list);
        gridView.setAdapter(imagesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String movieData = imagesAdapter.getItem(position).toString();
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movieData);
                startActivity(intent);
            }
        });


        return rootView;
    }

    private void updateList() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateList();
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(Void... params) {
            // TODO : remove the zip code thing

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortType = sharedPrefs.getString(
                    getString(R.string.pref_sort_key),
                    getString(R.string.pref_units_popularity));

            try {
                final String MOVIE_LIST_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_FORMAT_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_LIST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_FORMAT_PARAM, sortType)
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIESDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.i(LOG_TAG, builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }


                if (buffer.length() == 0) {
                    return null;
                }
                moviesJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error while closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        private String[] getMovieDataFromJson(String moviesJsonStr) throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESULT_LIST = "results";
            final String TITLE = "title";
            final String POSTER_PATH = "poster_path";
            final String RELEASE_DATE = "release_date";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w500";
            final String SEPARATOR = " ::: ";

            JSONObject movieJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = movieJson.getJSONArray(RESULT_LIST);

            String[] resultStrs = new String[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {

                JSONObject movieObject = moviesArray.getJSONObject(i);
                String title = movieObject.getString(TITLE);
                String posterPath = MOVIE_POSTER_BASE_URL + movieObject.getString(POSTER_PATH);
                String releaseDate = movieObject.getString(RELEASE_DATE);
                String voteAverage = movieObject.getString(VOTE_AVERAGE);
                String overview = movieObject.getString(OVERVIEW);


                resultStrs[i] = title + SEPARATOR + posterPath + SEPARATOR + releaseDate + SEPARATOR + voteAverage + SEPARATOR + overview;
            }
            return resultStrs;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                imagesAdapter.clear();
                for (String i : result) {
                    imagesAdapter.add(i);
                }
            }
        }

    }
}
