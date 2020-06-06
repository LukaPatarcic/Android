package com.jelena.myapplication;

import android.graphics.Bitmap;

public class MovieItem {

    private Bitmap imageResource;
    private String title;
    private String year;
    private String type;

    public MovieItem(Bitmap newImageResource, String newTitle, String newYear, String newType) {
        imageResource = newImageResource;
        title = newTitle;
        year = newYear;
        type = newType;
    }

    public Bitmap getImageResource() {
        return imageResource;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getType() {
        return type;
    }
}
