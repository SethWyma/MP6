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
    IntentIntegrator qrScanner;
    String[] qrArrayData;
    String[] friendlyText;
    TextView badQrText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.NoActionBar);

        Button generateQR = findViewById(R.id.generateQR);
        Button scanQR = findViewById(R.id.scanQR);
        badQrText = findViewById(R.id.badQrText);
        context = this.getApplicationContext();
        badQrText.setVisibility(View.INVISIBLE);


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
                        .setPrompt("Place QR code in viewfinder to scan.")
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
                String contents = result.getContents();
                qrArrayData = contents.split(Character.toString((char) 31));
                Vibrator scanSuccessVibration = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    scanSuccessVibration.vibrate(VibrationEffect.createOneShot(750, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    scanSuccessVibration.vibrate(750);
                }
                try {
                    friendlyText = qrDataDisplayBuilder(qrArrayData);
                } catch (ArrayIndexOutOfBoundsException | ParseException e) {
                    friendlyText = null;
                }
                if (friendlyText != null && friendlyText.length == 5) {
                    badQrText.setVisibility(View.INVISIBLE);
                    Intent successfulScan = new Intent(MainActivity.this, SuccessfulScan.class);
                    successfulScan.putExtra("friendly text", friendlyText);
                    successfulScan.putExtra("qr data", qrArrayData);
                    startActivity(successfulScan);
                } else {
                    badQrText.setVisibility(View.VISIBLE);
                    return;
                }
            } else {
                Toast.makeText(this, "Scan canceled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String[] qrDataDisplayBuilder(String[] qrArrayData) throws ParseException {
        /**
         * Name of event
         * Day of wk, MMMMM dd, yyyy
         * Starts at: hh:mm
         * Ends at: hh:mm
         * (If different day) On Day of wk, MMMMM, dd, yyyy
         * At [location]
         * [Description]
         */

        String[] friendlyTextArray = new String[5];
        friendlyTextArray[0] = qrArrayData[0];

        SimpleDateFormat isoParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        Date startDate;
        Date endDate;
        startDate = isoParser.parse(qrArrayData[1]);
        endDate = isoParser.parse(qrArrayData[2]);

        // Clunky way to check if start and end dates are same.
        if (startDate.toString().substring(0, 10).equals(endDate.toString().substring(0, 10))) {
            friendlyTextArray[1] = (new SimpleDateFormat("EEEE, MMMM d, yyyy").format(startDate));
            friendlyTextArray[2] = (timeFormatter.format(startDate) + " to " + timeFormatter.format(endDate));
        } else {
            friendlyTextArray[1] = (new SimpleDateFormat("EEEE, MMMM d, yyyy").format(startDate)
                    + " at " + timeFormatter.format(startDate));
            friendlyTextArray[2] = ("to " + new SimpleDateFormat("EEEE, MMMM d, yyyy").format(endDate)
                    + " at " + timeFormatter.format(endDate));
        }

        if (qrArrayData.length > 3) {
            friendlyTextArray[3] = "At " + qrArrayData[3];
        }
        if (qrArrayData.length > 4) {
            friendlyTextArray[4] = qrArrayData[4];
        }
        return friendlyTextArray;
    }
}
