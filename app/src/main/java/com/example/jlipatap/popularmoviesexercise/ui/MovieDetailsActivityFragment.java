package com.example.jlipatap.popularmoviesexercise.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jlipatap.popularmoviesexercise.R;
import com.example.jlipatap.popularmoviesexercise.api.TmdbApi;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailsActivityFragment extends Fragment {

    String LOG_TAG = "MovieDetailsActivityFragment";
    JSONObject mJsonObject;

    public MovieDetailsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get JSON String with movie detail data from Intent Extra
        try {
            mJsonObject = new JSONObject(getActivity().getIntent().getStringExtra("jsonString"));
            String jsonObjStr = mJsonObject.toString();
            Log.d(LOG_TAG, "jsonObjStr = " + jsonObjStr);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON ERROR", e);
            e.printStackTrace();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        Log.d(LOG_TAG, mJsonObject.toString());

        //Create references to UI elements
        TextView uiMovieTitle = (TextView) rootView.findViewById(R.id.moviedetails_movietitle);
        ImageView uiPosterImage = (ImageView) rootView.findViewById(R.id.moviedetails_posterimage);
        TextView uiReleaseDate = (TextView) rootView.findViewById(R.id.moviedetails_releasedate);
        TextView uiUserRating = (TextView) rootView.findViewById(R.id.moviedetails_userrating);
        TextView uiPlotSynopsis = (TextView) rootView.findViewById(R.id.moviedetails_plotsynopsis);


        //Update UI elements with JSON data
        try {
            //Update text fields
            uiMovieTitle.setText(mJsonObject.getString("title"));
            uiReleaseDate.setText(mJsonObject.getString("release_date")
                    .substring(0, 4)); //Truncate string to only show year (first 4 characters)
            uiUserRating.setText(mJsonObject.getString("vote_average") + "/10");
            uiPlotSynopsis.setText(mJsonObject.getString("overview"));

            //Update ImageView
            String imageUrl = TmdbApi.TMDB_GETIMAGE_BASEURL + TmdbApi.TMDB_IMAGE_SIZE
                    + mJsonObject.getString("poster_path");
            Log.d(LOG_TAG, "imageURL = "+imageUrl);
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .into(uiPosterImage);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON ERROR", e);
            e.printStackTrace();
            Util.showToast("JSON ERROR", getActivity());
        } catch (StringIndexOutOfBoundsException e){
            Log.e(LOG_TAG, "ERROR", e);
            Util.showToast("JSON ERROR", getActivity());

        }

        return rootView;
    }
}
