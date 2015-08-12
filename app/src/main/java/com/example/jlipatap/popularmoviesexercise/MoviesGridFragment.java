package com.example.jlipatap.popularmoviesexercise;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesGridFragment extends Fragment {

    //Declarations
    public String LOG_TAG = MoviesGridFragment.class.getSimpleName(); //For logcat
    GridView gridview; //Layout to display movies in a grid
    ImageView imageView; //View to show movie poster images
    private ImageAdapter mImageAdapter; //ImageAdapter for GridView
    JSONObject mMainJsonStrObject; //For API response from TMDB
    JSONArray mJsonArray; //Parsed 'results' array from API response
    String[] mMoviePosterPaths; //Parsed image paths from API response
    //String mMovieSortSetting = TmdbApiHandler.TMDB_SORT_BY_POPULARITY; // TODO Sort setting must be user-definable
    String mMovieSortSetting;

    public MoviesGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //Get SharedPreferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mMovieSortSetting = sharedPref.getString("pref_sortSetting", "NULL");
        Log.d(LOG_TAG, "sharedPrefMovieSortSetting = " + mMovieSortSetting);

        //Get movies from API and display to GridView
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(mMovieSortSetting);

    }

    public void onStart() {
        super.onStart();

    }

    public void onResume(){
        super.onResume();

        //Get SharedPreferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mMovieSortSetting = sharedPref.getString("pref_sortSetting", "NULL");
        Log.d(LOG_TAG, "sharedPrefMovieSortSetting = " + mMovieSortSetting);


        //Get movies from API and display to GridView
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(mMovieSortSetting);
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

                /*Toast.makeText(getActivity(), Integer.toString(position),
                        Toast.LENGTH_SHORT).show();*/

                //Get movie details from member JSON Array (previously fetched in FetchMoviesTask)
                JSONObject jsonObject = null;
                try {
                    jsonObject = mJsonArray.getJSONObject(position);
                } catch(JSONException e){
                    Log.e(LOG_TAG,"JSON ERROR",e);
                    e.printStackTrace();
                }

                //If JSON array retrieved successfully, put into Extra and launch Movie Details Activity
                if(jsonObject!=null) {
                    Intent showMovieDetailsIntent = new Intent(getActivity(), MovieDetailsActivity.class);
                    showMovieDetailsIntent.putExtra("jsonString", jsonObject.toString());
                    startActivity(showMovieDetailsIntent);
                } else {
                    Toast.makeText(getActivity(), "No JSON Data Available",
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
            mMovieSortSetting = sharedPref.getString("pref_sortSetting", "NULL");
            Log.d(LOG_TAG, "sharedPrefMovieSortSetting = " + mMovieSortSetting);

            //Fetch movies from API and display to GridView
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute(mMovieSortSetting);
        }

        return super.onOptionsItemSelected(item);

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Void>{

        @Override
        protected void onPreExecute() {
            Toast.makeText(getActivity(), "Connecting to TMDB...",Toast.LENGTH_SHORT).show();
        }

        protected Void doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                Uri builtUri = Uri.parse(TmdbApiHandler.TMDB_GETMOVIES_BASEURL).buildUpon()
                        .appendQueryParameter("api_key", TmdbApiHandler.TMDB_API_KEY)
                        .appendQueryParameter("sort_by", params[0]).build();
                Log.d(LOG_TAG,"builtUri: "+builtUri.toString());
                URL url = new URL(builtUri.toString());

                // Create the request to TMDB and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                Log.d(LOG_TAG, "urlConnection.connect() called " + url.toString());
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    movieJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    movieJsonStr = null;
                }
                movieJsonStr = buffer.toString();
                Log.d(LOG_TAG, movieJsonStr); //Comment this out when done.  This is going to be huge.

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                movieJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try
            {
                mMainJsonStrObject = new JSONObject(movieJsonStr);
                mJsonArray = mMainJsonStrObject.getJSONArray("results");
                mMoviePosterPaths = new String[mJsonArray.length()];
                Log.d(LOG_TAG, "mMainJsonStrObject + array + mMoviePosterPaths created");
                Log.d(LOG_TAG, "mMoviePosterPaths.length = " + Integer.toString(mMoviePosterPaths.length));

                //Parse movie poster paths from JSON Array
                for(int i=0; i< mJsonArray.length(); i++){
                    JSONObject childJsonObject = mJsonArray.getJSONObject(i);
                    mMoviePosterPaths[i] = childJsonObject.getString("poster_path");
                    Log.d(LOG_TAG, "mMoviePosterPaths "+i+" "+ mMoviePosterPaths[i]);
                }
            } catch(JSONException e){
                Log.e(LOG_TAG,"JSON ERROR",e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(LOG_TAG,"onPostExecute()");
            mImageAdapter.notifyDataSetChanged();
        }
    }

    public class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }


        public int getCount() {
            try{
                Log.v(LOG_TAG, "BaseAdatper getCount()" + Integer.toString(mMoviePosterPaths.length));
                return mMoviePosterPaths.length;
            } catch (NullPointerException nullPointerException){
                Log.v(LOG_TAG, "BaseAdapter NPE caught");
                return 1;
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
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }

           //imageView.setImageResource(R.drawable.dummy);

            if(mMoviePosterPaths!=null) {
               String imageUrl = TmdbApiHandler.TMDB_GETIMAGE_BASEURL + TmdbApiHandler.TMDB_IMAGE_SIZE
                       + mMoviePosterPaths[position];
                //Log.d(LOG_TAG, imageUrl);

                //Log.d(LOG_TAG,"Picasso() "+imageUrl);

                Picasso.with(mContext)
                    .load(imageUrl)//.resize(185*2,278*2)
                    .into(imageView);
            }
            return imageView;
        }

    }
}
