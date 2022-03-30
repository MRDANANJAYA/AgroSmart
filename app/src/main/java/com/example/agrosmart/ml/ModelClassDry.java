package com.example.agrosmart.ml;

import android.net.Uri;

public class ModelClassDry {



        String imagenameDry;
        Uri imageDry;

        public ModelClassDry() {
        }


        public ModelClassDry(String imagenamedry, Uri imagedry) {
            this.imagenameDry = imagenamedry;
            this.imageDry = imagedry;
        }


        public Uri getImage() {
            return imageDry;
        }

        public void setImage(Uri image) {
            this.imageDry = image;
        }

        public String getImagename() {
            return imagenameDry;
        }

        public void setImagename(String imagename) {
            this.imagenameDry = imagename;
        }

}
