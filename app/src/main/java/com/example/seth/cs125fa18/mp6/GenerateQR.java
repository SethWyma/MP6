package com.example.seth.cs125fa18.mp6;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
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
import java.util.Locale;

public class GenerateQR extends AppCompatActivity {

    String qrData;
    TextView badInputMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.NoActionBar);
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
        badInputMessage = findViewById(R.id.badInputMessage);
        Button createQr = findViewById(R.id.createQrButton);

        badInputMessage.setVisibility(View.INVISIBLE);
        badInputMessage.setMovementMethod(new ScrollingMovementMethod());
        editEndDate.setVisibility(View.GONE);

        // Set date input box to current date.
        Date currentDate = Calendar.getInstance().getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        sdf.setLenient(false);
        final String formattedDate = sdf.format(currentDate);
        editStartDate.setText(formattedDate);

        // Hides or reveals editEndDate
        endsDifferentDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editEndDate.getVisibility() == View.GONE) {
                    try {
                        final Calendar mCalendar = Calendar.getInstance();
                        Date currentStartDate = sdf.parse(editStartDate.getText().toString());
                        mCalendar.setTime(currentStartDate);
                        mCalendar.add(Calendar.DAY_OF_YEAR, 1);
                        editEndDate.setText(sdf.format(mCalendar.getTime()));
                    } catch(ParseException e) {
                        editEndDate.setText(editStartDate.getText());
                    }
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

                if (editName.getText().toString().length() > maxLength) {
                    badInputText += "\n Name of Event is too long.";
                }
                if (editName.getText().toString().length() == 0) {
                    badInputText +="\n There is no event name.";
                }
                if (editDescription.getText().toString().length() > maxLength) {
                    badInputText += "\n Description is too long.";
                }
                if (editLocation.getText().toString().length() > maxLength) {
                    badInputText += "\n Location name is too long.";
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
                    badInputText += "\n Start date or start time is invalid.";
                }
                if (endDateTime == "invalid") {
                    badInputText += "\n End date or end time is invalid.";
                }
                if (startDateTime != "invalid" && endDateTime != "invalid") {
                    SimpleDateFormat formattedToDatesdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    try {
                        Date start = formattedToDatesdf.parse(startDateTime);
                        Date end = formattedToDatesdf.parse(endDateTime);
                        if (start.after(end)) {
                            badInputText += "\n End of event is after the start!";
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (badInputText.length() > 0) {
                    badInputMessage.setText("Issues with input:" + badInputText);
                    badInputMessage.setVisibility(View.VISIBLE);
                    return;
                } else {
                    String[] rawData = new String[]{
                            editName.getText().toString(),
                            startDateTime,
                            endDateTime,
                            editLocation.getText().toString(),
                            editDescription.getText().toString()
                    };

                    char unit_separator = (char) 31;
                    for (String item : rawData) {
                        qrData = qrData + item + unit_separator;
                    }
                    qrData = qrData.substring(4, qrData.length() - 2);

                    // Sample data: "Fun Party!&#@42018-12-07T17:30:00-06:00&#@42018-12-07T20:00:00-06:00&#@4123 Maple Ave.&#@4Enjoy time w/ friends at my house."

                    Intent startDisplayQr = new Intent(GenerateQR.this, DisplayQR.class);
                    startDisplayQr.putExtra("eventDataKey", qrData);
                    startActivity(startDisplayQr);
                    finish();
                }
            }
        });
    }

    private String attemptToParse(String date) {
        try {
            StringBuffer sb = new StringBuffer(32);
            // "hh:mm" shouldn't be correct, but it would always throw an error at PM times when I used "HH:mm".
            SimpleDateFormat dfHelper = new SimpleDateFormat("MM-dd-yyyy hh:mm aa", Locale.ENGLISH);
            dfHelper.setLenient(false);
            Date resultDate = dfHelper.parse(date);
            dfHelper.applyPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
            return dfHelper.format(resultDate, sb, new FieldPosition(0)).toString();
        } catch (ParseException e) {
            return "invalid";
        }
    }
}
