package com.example.jlipatap.popularmoviesexercise;

import com.example.jlipatap.popularmoviesexercise.api.APIKey;

public class TmdbApiHandler {

    public static String TMDB_GETMOVIES_BASEURL = "http://api.themoviedb.org/3/discover/movie?";
    public static String TMDB_GETIMAGE_BASEURL = "http://image.tmdb.org/t/p/";
    public static String TMDB_API_KEY = APIKey.getKey();    //This is stored in a separate class because it cannot be uploaded to GIT
    public static String TMDB_SORT_BY_POPULARITY = "popularity.desc";
    public static String TMDB_SORT_BY_RATING = "vote_average.desc";
    public static String TMDB_IMAGE_SIZE = "w342";


}
