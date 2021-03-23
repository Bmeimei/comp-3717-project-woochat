package com.example.woochat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromUrl(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Can not load the image!", e.getMessage());
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
}
