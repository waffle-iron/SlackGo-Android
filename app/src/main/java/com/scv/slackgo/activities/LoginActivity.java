package com.scv.slackgo.activities;

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

import com.scv.slackgo.R;
import com.scv.slackgo.helpers.Constants;
import com.scv.slackgo.helpers.Preferences;

import java.io.InputStream;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView slackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        String slackCode = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString(Constants.SLACK_TOKEN, null);

        if (slackCode == null) {

            slackButton = (ImageView) findViewById(R.id.slackButton);
            String slackLink = getString(R.string.slack_image_link);
            new DownloadImageTask(slackButton).execute(slackLink);
            slackButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Uri slackUri = Uri.parse(String.format(getString(R.string.slack_link),
                            getString(R.string.slack_scope), getString(R.string.client_id), getString(R.string.redirect_oauth)));

                    Intent intent = new Intent(Intent.ACTION_VIEW, slackUri);

                    startActivity(intent);
                }
            });
        } else {

            Intent nextIntent;
            if (Preferences.areRegionsEmpty(this, getString(R.string.preferences_regions_list))) {
                nextIntent = new Intent(this, DetailRegionActivity.class);
            } else {
                nextIntent = new Intent(this, RegionsActivity.class);
            }

            startActivity(nextIntent);
        }
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

