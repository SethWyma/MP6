package com.example.seth.cs125fa18.mp6;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Context context;
    TextView eventInfo;
    IntentIntegrator qrScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button generateQR = findViewById(R.id.generateQR);
        Button scanQR = findViewById(R.id.scanQR);
        context = this.getApplicationContext();
        eventInfo = findViewById(R.id.eventInfo);

        qrScanner = new IntentIntegrator(this);

        generateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GenerateQR.class));
            }
        });

        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        .setOrientationLocked(false)
                        .setBeepEnabled(false);
                qrScanner.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                Vibrator scanSuccessVibration = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    scanSuccessVibration.vibrate(VibrationEffect.createOneShot(750, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    scanSuccessVibration.vibrate(750);
                }
                String contents = result.getContents();
                String[] qrArrayData = contents.split(Character.toString((char) 31));
                qrDataDisplayBuilder(qrArrayData);
            } else {
                Toast.makeText(this, "Scan canceled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void qrDataDisplayBuilder(String[] qrArrayData) {
        /**
         * Name of event
         * Day of wk, MMMMM dd, yyyy
         * Starts at: hh:mm
         * Ends at: hh:mm
         * (If different day) On Day of wk, MMMMM, dd, yyyy
         * At: [location]
         * Description: [Description]
         */
        eventInfo.append(qrArrayData[0] + "\n");

        SimpleDateFormat isoParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        Date startDate;
        Date endDate;
        try {
            startDate = isoParser.parse(qrArrayData[1]);
            endDate = isoParser.parse(qrArrayData[2]);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        // Clunky way to check if start and end dates are same.
        if (startDate.toString().substring(0, 10).equals(endDate.toString().substring(0, 10))) {
            eventInfo.append(new SimpleDateFormat("EEEE, MMMM d, yyyy").format(startDate) + "\n");
            eventInfo.append(timeFormatter.format(startDate) + " to " + timeFormatter.format(endDate) + "\n");
        } else {
            eventInfo.append("From: " + new SimpleDateFormat("EEEE, MMMM d, yyyy").format(startDate)
                    + " at " + timeFormatter.format(startDate) + "\n");
            eventInfo.append("To: " + new SimpleDateFormat("EEEE, MMMM d, yyyy").format(endDate)
                    + " at " + timeFormatter.format(endDate) + "\n");
        }

        if (qrArrayData.length > 3) {
            eventInfo.append("Location: " + qrArrayData[3] + "\n");
        }
        if (qrArrayData.length > 4) {
            eventInfo.append("Description: " + qrArrayData[4] + "\n");
        }
    }
}
