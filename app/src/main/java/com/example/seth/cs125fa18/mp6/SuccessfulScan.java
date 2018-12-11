package com.example.seth.cs125fa18.mp6;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SuccessfulScan extends AppCompatActivity {

    String[] qrData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.NoActionBar);
        setContentView(R.layout.successful_scan);

        TextView title = findViewById(R.id.title);
        TextView datetimes = findViewById(R.id.datetimes);
        TextView location = findViewById(R.id.location);
        TextView description = findViewById(R.id.description);
        Button addToCalendar = findViewById(R.id.addToCalendar);

        Bundle extras = getIntent().getExtras();
        String[] friendlyTextArray = (extras.getStringArray("friendly text"));
        qrData = extras.getStringArray("qr data");

        title.setText(friendlyTextArray[0]);
        datetimes.setText(friendlyTextArray[1] + "\n" + friendlyTextArray[2]);
        location.setText(friendlyTextArray[3]);
        description.setText(friendlyTextArray[4]);


        addToCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventToCalendar(qrData);
            }
        });
    }

    public void addEventToCalendar(String[] eventData) {
        Intent calendarIntent = new Intent(Intent.ACTION_INSERT);
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        long startTime = 0;
        long endTime = 0;
        try {
            startTime = parser.parse(eventData[1]).getTime();
            endTime = parser.parse(eventData[2]).getTime();
        } catch (ParseException e) {
            // This shouldn't happen
            e.printStackTrace();
            return;
        }
        calendarIntent.setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, eventData[0])
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);

        if (eventData.length > 3) {
            calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, eventData[3]);
        }
        if (eventData.length > 4) {
            calendarIntent.putExtra(CalendarContract.Events.DESCRIPTION, eventData[4]);
        }
        startActivity(calendarIntent);
    }
}
