package com.example.seth.cs125fa18.mp6;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class GenerateQR extends AppCompatActivity {

    String qrData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr);

        Button createQr = (Button) findViewById(R.id.createQrButton);

        createQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrData = "test_text";
                Intent startDisplayQr = new Intent(GenerateQR.this, DisplayQR.class);
                startDisplayQr.putExtra("eventDataKey", qrData);
                startActivity(startDisplayQr);
            }
        });
    }
}
