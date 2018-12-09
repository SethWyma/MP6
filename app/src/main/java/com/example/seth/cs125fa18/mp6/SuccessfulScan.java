package com.example.seth.cs125fa18.mp6;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class SuccessfulScan extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.NoActionBar);
        setContentView(R.layout.successful_scan);

        TextView title = findViewById(R.id.title);
        TextView firstDatetime = findViewById(R.id.firstDatetime);
        TextView secondDatetime = findViewById(R.id.secondDatetime);
        TextView location = findViewById(R.id.location);
        TextView description = findViewById(R.id.description);
        Button addToCalendar = findViewById(R.id.addToCalendar);

        Bundle extras = getIntent().getExtras();
        String[] friendlyTextArray = (extras.getStringArray("friendly text"));
        String[] qrData = extras.getStringArray("qr data");

        title.setText(friendlyTextArray[0]);
        firstDatetime.setText(friendlyTextArray[1] + "\n" + friendlyTextArray[2]);
        //secondDatetime.setText(friendlyTextArray[2]);
        location.setText(friendlyTextArray[3]);
        description.setText(friendlyTextArray[4]);
    }
}
