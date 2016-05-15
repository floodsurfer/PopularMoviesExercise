package com.example.jlipatap.popularmoviesexercise.api;

import android.util.Log;

import com.example.jlipatap.popularmoviesexercise.model.ApiResponse;
import com.example.jlipatap.popularmoviesexercise.ui.MoviesGridFragment;
import com.example.jlipatap.popularmoviesexercise.ui.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jlipatap on 7/18/15.
 */
public class TmdbApi {

    private static final String LOG_TAG = TmdbApi.class.getSimpleName();

    // API strings
    public static final String BASE_URL = "http://api.themoviedb.org/";
    public static String TMDB_GETIMAGE_BASEURL = "http://image.tmdb.org/t/p/";
    public static String TMDB_IMAGE_SIZE = "w342";
    public static String TMDB_SORT_BY_POPULARITY = "popularity.desc";
    public static String TMDB_SORT_BY_RATING = "vote_average.desc";

    MoviesGridFragment mMoviesGridFragment;

    public TmdbApi(MoviesGridFragment moviesGridFragment) {
        this.mMoviesGridFragment = moviesGridFragment;
    }

    public void getMovies(String movieSortSetting){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MyApiEndpointInterface apiService =
                retrofit.create(MyApiEndpointInterface.class);

        Call<ApiResponse> call = apiService.getMovies(movieSortSetting, APIKey.getKey());
        Log.d(LOG_TAG, "Retrofit call");
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                // WORKING
                ApiResponse apiResponse = response.body();
                String tellUser = "Retrofit received API results, "+ Integer.toString(apiResponse.getResults().size());
                Log.d(LOG_TAG, tellUser);

                mMoviesGridFragment.setMovies(apiResponse);
                mMoviesGridFragment.updateAdapter();

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(LOG_TAG, "Retrofit failure");

                Util.showToast("API failure", mMoviesGridFragment.getActivity());

            }
        });
    }


}
