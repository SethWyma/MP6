package com.example.seth.cs125fa18.mp6;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;


public class DisplayQR extends AppCompatActivity {

    String qrData;
    TextView statusText;
    Drawable qrCodeDrawable;
    ImageView qrCode;
    Button returnToMain;

    private class getQrData extends AsyncTask<String, Void, Drawable> {
        protected Drawable doInBackground(String... urls) {
            Drawable qrDrawable = null;
            try {
                URL url = new URL(urls[0]);
                InputStream qrCodeStream = (InputStream) url.getContent();
                qrDrawable = Drawable.createFromStream(qrCodeStream, "src name");
            } catch(Exception e) {
                e.printStackTrace();
                statusText.setText("Could not get image.");
            }
            return qrDrawable;
        }

        protected void onPostExecute(Drawable result) {
            if (result != null) {
                statusText.setVisibility(View.GONE);
            }
            qrCodeDrawable = result;
            qrCode.setImageDrawable(qrCodeDrawable);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.NoActionBar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            qrData = extras.getString("eventDataKey");
        }

        setContentView(R.layout.activity_display_qr);
        statusText = findViewById(R.id.statusText);
        qrCode = findViewById(R.id.qrCode);
        returnToMain = findViewById(R.id.returnToMain);

        String qrApiUrl = "https://chart.googleapis.com/chart?cht=qr&chl=" + qrData + "&chs=500x500&chld=L|0";
        new getQrData().execute(qrApiUrl);

        returnToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
