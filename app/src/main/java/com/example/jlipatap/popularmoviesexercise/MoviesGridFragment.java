package com.example.jlipatap.popularmoviesexercise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jlipatap.popularmoviesexercise.api.TmdbApi;
import com.example.jlipatap.popularmoviesexercise.model.ApiResponse;
import com.example.jlipatap.popularmoviesexercise.model.Result;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesGridFragment extends Fragment {

    public String LOG_TAG = MoviesGridFragment.class.getSimpleName(); //For logcat

    // UI
    GridView gridview; //Layout to display movies in a grid
    ImageView imageView; //View to show movie poster images
    private ImageAdapter mImageAdapter; //ImageAdapter for GridView

    // User settings
    String mMovieSortSetting;

    // Retrofit
    TmdbApi mTmdbApi;
    ApiResponse mApiResponse;


    public MoviesGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //Get SharedPreferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mMovieSortSetting = sharedPref.getString(getString(R.string.pref_sortSetting_key), getString(R.string.pref_sortSetting_default));
        Log.d(LOG_TAG, "sharedPrefMovieSortSetting = " + mMovieSortSetting);

        // Get movie data from TMDB API
        mTmdbApi = new TmdbApi(this);
        mTmdbApi.getMovies(mMovieSortSetting);

    }

    public void onStart() {
        super.onStart();

    }

    public void onResume(){
        super.onResume();

        if(mApiResponse==null){
            mTmdbApi.getMovies(mMovieSortSetting);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        gridview = (GridView) rootView.findViewById(R.id.grid_view);
        mImageAdapter = new ImageAdapter(getActivity());
        gridview.setAdapter(mImageAdapter);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Result movieItem = mApiResponse.getResults().get(position);

                //If movieItem retrieved successfully, put into Extra and launch Movie Details Activity
                if(movieItem!=null) {
                    Intent showMovieDetailsIntent = new Intent(getActivity(), MovieDetailsActivity.class);

                    // Serialize movieItem
                    String movieItemJSON = new Gson().toJson(movieItem);

                    showMovieDetailsIntent.putExtra("jsonString", movieItemJSON);
                    startActivity(showMovieDetailsIntent);
                } else {
                    Toast.makeText(getActivity(), "No Data Available",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        return rootView;
    }

    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){ //Not sure where "menu" is coming
        // from.  Is this just something the framework passes when calling this method?

        inflater.inflate(R.menu.movieslayout, menu);
        //return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_refresh){

            //Get SharedPreferences
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mMovieSortSetting = sharedPref.getString(getString(R.string.pref_sortSetting_key), getString(R.string.pref_sortSetting_default));
            Log.d(LOG_TAG, "sharedPrefMovieSortSetting = " + mMovieSortSetting);

            //Fetch movies from API and display to GridView
            //FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            //fetchMoviesTask.execute(mMovieSortSetting);


        }

        return super.onOptionsItemSelected(item);

    }

    // UI control methods
    public void showToast(String text){
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    public void setMovies(ApiResponse apiResponse){
        this.mApiResponse = apiResponse;
    }

    public void updateAdapter(){
        mImageAdapter.notifyDataSetChanged();
    }

    // Adapter for GridView
    public class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }


        public int getCount() {
            try{
                return mApiResponse.getResults().size();
            } catch (NullPointerException nullPointerException){
                Log.v(LOG_TAG, "BaseAdapter NPE caught");
                return 0;
            }
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setAdjustViewBounds(true);
            } else {
                imageView = (ImageView) convertView;
            }

            String moviePosterPath = mApiResponse.getResults().get(position).getPosterPath();

            if(moviePosterPath!=null) {

                String url = TmdbApiHandler.TMDB_GETIMAGE_BASEURL + TmdbApiHandler.TMDB_IMAGE_SIZE
                        + moviePosterPath;

                Log.d(LOG_TAG,"Picasso() "+url);

                Picasso.with(mContext)
                    .load(url)//.resize(185*2,278*2)
                    .into(imageView);
            }
            return imageView;
        }

    }
}
