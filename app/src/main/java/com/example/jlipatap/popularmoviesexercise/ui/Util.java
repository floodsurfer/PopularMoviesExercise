package com.example.jlipatap.popularmoviesexercise.ui;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by jlipata on 5/15/16.
 */
public class Util {

    public static void showToast(String text, Context context){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

}
