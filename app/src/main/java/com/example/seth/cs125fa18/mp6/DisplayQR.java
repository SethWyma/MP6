package com.example.seth.cs125fa18.mp6;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;


public class DisplayQR extends AppCompatActivity {

    String qrData;
    TextView statusText;
    Drawable qrCodeDrawable;
    ImageView qrCode;

    private class getQrData extends AsyncTask<String, Void, Drawable> {
        protected Drawable doInBackground(String... urls) {
            Drawable qrDrawable = null;
            try {
                URL url = new URL(urls[0]);
                InputStream qrCodeStream = (InputStream) url.getContent();
                qrDrawable = Drawable.createFromStream(qrCodeStream, "src name");
            } catch(Exception e) {
                e.printStackTrace();
                Log.d("INTERNET ERROR", "Didn't get image.");
            }
            return qrDrawable;
        }

        protected void onPostExecute(Drawable result) {
            qrCodeDrawable = result;
            qrCode.setImageDrawable(qrCodeDrawable);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            qrData = extras.getString("eventDataKey");
        }

        setContentView(R.layout.activity_display_qr);
        statusText = (TextView) findViewById(R.id.statusText);
        qrCode = (ImageView) findViewById(R.id.qrCode);
        String qrApiUrl = "https://chart.googleapis.com/chart?cht=qr&chl=" + qrData + "&chs=500x500&chld=L|0";
        /**
        try {
            InputStream qrCodeStream = (InputStream) new URL(qrApiUrl).getContent();
            Drawable qrDrawable = Drawable.createFromStream(qrCodeStream, "name?");
            qrCode.setImageDrawable(qrDrawable);
        } catch(Exception e) {
            e.printStackTrace();
            Log.d("INTERNET ERROR", "Didn't get image.");
        }
         */
        new getQrData().execute(qrApiUrl);
    }
}
