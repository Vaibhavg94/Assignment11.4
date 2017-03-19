package com.vaibhav.assignment114;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private ProgressBar progressBar;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        image = (ImageView) findViewById(R.id.imageView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setProgress(10);

                // Execute DownloadImage AsyncTask
                String URL = editText.getText().toString();
                new DownloadImage().execute(URL);
            }
        });
    }

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected void onPreExecute() {

            image.setImageResource(R.drawable.placeholder);
            super.onPreExecute();
            progressBar.setMax(100);
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];
            int count;

            try {
                URL url = new URL(imageURL);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 81920);

                // Output stream to write file
                OutputStream output = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "downloadedfile.jpg"));

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress((int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/downloadedfile.jpg";
            // setting downloaded into image view
            image.setImageDrawable(Drawable.createFromPath(imagePath));
        }
    }
}
