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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.google.android.gms.common.api.GoogleApiClient;
import java.io.InputStream;

/**
 * Created by ayelen@scvsoft.com .
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private ImageView slackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slackButton = (ImageView) findViewById(R.id.slackButton);
        new DownloadImageTask(slackButton).execute("https://platform.slack-edge.com/img/sign_in_with_slack@2x.png");
        slackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri slackUri = Uri.parse("https://slack.com/oauth/authorize?" +
                        "scope=identity.basic&client_id=2946387922.79631581941&redirect_uri=scv%3A%2F%2Fauthorize");

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
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

