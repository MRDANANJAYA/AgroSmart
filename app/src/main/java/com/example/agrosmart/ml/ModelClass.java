package com.example.agrosmart.ml;

import android.net.Uri;


public class ModelClass {

    String imagename;
    Uri image;

    public ModelClass() {
    }


    public ModelClass(String imagename, Uri image) {
        this.imagename = imagename;
        this.image = image;
    }


    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }
}