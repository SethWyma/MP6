package com.example.seth.cs125fa18.mp6;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class GenerateQR extends AppCompatActivity {

    String qrData;
    TextView badInputMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr);

        final EditText editName = findViewById(R.id.editName);
        final EditText editStartDate = findViewById(R.id.editStartDate);
        final EditText editEndDate = findViewById(R.id.editEndDate);
        final CheckBox endsDifferentDay = findViewById(R.id.endsDifferentDay);
        final EditText editStartTime = findViewById(R.id.editStartTime);
        final EditText editEndTime = findViewById(R.id.editEndTime);
        final ToggleButton toggleStartAmPm = findViewById(R.id.toggleStartAmPm);
        final ToggleButton toggleEndAmPm = findViewById(R.id.toggleEndAmPm);
        final EditText editLocation = findViewById(R.id.editLocation);
        final EditText editDescription = findViewById(R.id.editDescription);
        badInputMessage = (TextView) findViewById(R.id.badInputMessage);
        Button createQr = findViewById(R.id.createQrButton);

        editEndDate.setVisibility(View.GONE);

        // Set date input box to current date.
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String formattedDate = sdf.format(currentDate);
        editStartDate.setText(formattedDate);
        editEndDate.setText(formattedDate);

        // Hides or reveals editEndDate
        endsDifferentDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editEndDate.getVisibility() == View.GONE) {
                    editEndDate.setVisibility(View.VISIBLE);
                } else {
                    editEndDate.setVisibility(View.GONE);
                }
            }
        });

        createQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int maxLength = 100;

                // Check inputs

                String badInputText = "";

                if (editName.getText().toString().length() >= maxLength) {
                    badInputText += "\nName of Event is too long.";
                }
                if (editName.getText().toString().length() == 0) {
                    badInputText +="\nThere is no event name.";
                }
                if (editDescription.getText().toString().length() >= maxLength) {
                    badInputText += "\nDescription is too long.";
                }


                String unformattedStartDate = editStartDate.getText().toString()
                        + " " + editStartTime.getText().toString()
                        + " " + toggleStartAmPm.getText().toString();
                String unformattedEndDate;
                if (endsDifferentDay.isChecked()) {
                    unformattedEndDate = editEndDate.getText().toString()
                            + " " + editEndTime.getText().toString()
                            + " " + toggleEndAmPm.getText().toString();
                } else {
                    unformattedEndDate = editStartDate.getText().toString()
                            + " " + editEndTime.getText().toString()
                            + " " + toggleEndAmPm.getText().toString();
                }

                String startDateTime = attemptToParse(unformattedStartDate);
                String endDateTime = attemptToParse(unformattedEndDate);

                if (startDateTime == "invalid") {
                    badInputText += "\nStart date or start time is invalid.";
                }
                if (endDateTime == "invalid") {
                    badInputText += "\nEnd date or end time is invalid.";
                }


                String[] rawData = new String[] {
                        editName.getText().toString(),
                        startDateTime,
                        endDateTime,
                        editLocation.getText().toString(),
                        editDescription.getText().toString()
                };

                for (String item : rawData) {
                    qrData += item + ", ";
                }
                qrData = qrData.substring(4, qrData.length() - 2);
                Intent startDisplayQr = new Intent(GenerateQR.this, DisplayQR.class);
                startDisplayQr.putExtra("eventDataKey", qrData);
                startActivity(startDisplayQr);
            }
        });
    }

    private String attemptToParse(String date) {
        try {
            StringBuffer sb = new StringBuffer(32);
            SimpleDateFormat dfHelper = new SimpleDateFormat("MM-dd-yyyy HH:mm aa");
            dfHelper.setLenient(false);
            Date resultDate = dfHelper.parse(date);
            dfHelper.applyPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
            String result = dfHelper.format(resultDate, sb, new FieldPosition(0)).toString();
            return result;
        } catch (ParseException e) {
            return "invalid";
        }
    }
}
