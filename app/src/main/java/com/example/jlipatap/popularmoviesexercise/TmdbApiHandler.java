package com.example.jlipatap.popularmoviesexercise;

/**
 * Created by jlipatap on 7/18/15.
 */
public class TmdbApiHandler {

    public static String TMDB_BASEURL = "http://api.themoviedb.org/3/discover/movie";
    public static String TMDB_API_KEY = APIKey.getKey();    //This is stored in a separate class because it cannot be uploaded to GIT
    public static String TMDB_SORT_BY_POPULARITY = "popularity.desc";
    public static String TMDB_SORT_BY_RATING = "vote_average.desc";


}
