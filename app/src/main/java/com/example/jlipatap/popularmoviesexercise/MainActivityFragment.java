package com.example.jlipatap.popularmoviesexercise;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.GridView;
import android.widget.Toast;

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
public class MainActivityFragment extends Fragment {

    public String LOG_TAG = "MainActivityFragment";

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

       // ArrayAdapter<Integer> arrayAdapter = new ImageAdapter<Integer>(getActivity(),R.layout.fragment_main, R.id.grid_view, sampleImagesArrayList);

        GridView gridview = (GridView) rootView.findViewById(R.id.grid_view);
        //gridview.setAdapter(arrayAdapter);

        gridview.setAdapter(new ImageAdapter(getActivity()));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), APIKey.getKey()+" " + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        /*GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(HelloGridView.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });*/

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

            String movieSortSetting = TmdbApiHandler.TMDB_SORT_BY_POPULARITY;

            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute(movieSortSetting);

            //AsyncTask asyncTask = new FetchMoviesTask().execute(); // Why isn't this type FetchMoviesTask
                    // instead of AsyncTask?  And why don't i need to pass Params to it as per api doc


        }

        return super.onOptionsItemSelected(item);

    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Void>{


        protected Void doInBackground(String... params) { //I don't understand this syntax.  Why "Void"
                // instead of "void"? How does "String... params" work?


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL for the API query TODO: Update this to URI
                //URL url = new URL("http://api.themoviedb.org/3/discover/movie?api_key="+ APIKey.getKey() +"&" +params[0]);

                final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter("api_key",APIKey.getKey())
                        .appendQueryParameter("sort_by",params[0]).build();
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
                JSONObject mainJsonStrObject = new JSONObject(movieJsonStr);
                JSONArray jsonArray = mainJsonStrObject.getJSONArray("results");
                String[] moviePosterPaths = new String[jsonArray.length()];
                Log.d(LOG_TAG,"mainJsonStrObject + array + moviePosterPaths created");

                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject childJsonObject = jsonArray.getJSONObject(i);
                    moviePosterPaths[i] = childJsonObject.getString("poster_path");
                    Log.d(LOG_TAG,"moviePosterPaths "+i+" "+moviePosterPaths[i]);
                }

            } catch(JSONException e){
                Log.e(LOG_TAG,"JSON ERROR",e);
                e.printStackTrace();

            }


            return null;
        }

    }
}
