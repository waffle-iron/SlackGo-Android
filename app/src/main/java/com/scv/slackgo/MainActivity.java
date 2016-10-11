package com.scv.slackgo;

/**
 * Created by ayelen@scvsoft.com
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by ayelen@scvsoft.com .
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView slackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slackButton = (ImageView) findViewById(R.id.slackButton);
        String slackLink = getString(R.string.slack_image_link);
        new DownloadImageTask(slackButton).execute(slackLink);
        slackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri slackUri = Uri.parse(String.format(getString(R.string.slack_link),
                        getString(R.string.slack_scope), getString(R.string.client_id), getString(R.string.redirect_oauth)));

                Intent intent = new Intent(Intent.ACTION_VIEW,slackUri);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.slackButton:
                slackButton.callOnClick();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String[] urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

