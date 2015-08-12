package com.example.jlipatap.popularmoviesexercise;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsActivityFragment extends Fragment {

    String LOG_TAG = "MovieDetailsActivityFragment";

    public MovieDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        TextView helloWorld = (TextView) rootView.findViewById(R.id.hello_world);

        //Toast.makeText(getActivity(), "helloWorld =" + helloWorld.toString(),
         //           Toast.LENGTH_SHORT).show();


        try {
            JSONObject jsonObj = new JSONObject(getActivity().getIntent().getStringExtra("jsonString"));
            String jsonObjStr = jsonObj.toString();
            Log.d(LOG_TAG, "jsonObjStr = " + jsonObjStr);
            helloWorld.setText(jsonObjStr);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON ERROR", e);
            e.printStackTrace();
        }



        return rootView;
    }
}
