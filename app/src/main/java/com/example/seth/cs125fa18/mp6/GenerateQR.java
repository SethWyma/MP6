package com.example.seth.cs125fa18.mp6;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

public class GenerateQR extends AppCompatActivity {

    String qrData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr);

        final EditText editName = (EditText) findViewById(R.id.editName);
        final EditText editDate = (EditText) findViewById(R.id.editDate);
        final EditText editDescription = (EditText) findViewById(R.id.editDescription);
        final EditText editStartTime = (EditText) findViewById(R.id.editStartTime);
        final EditText editEndTime = (EditText) findViewById(R.id.editEndTime);
        final ToggleButton toggleStartAmPm = (ToggleButton) findViewById(R.id.toggleStartAmPm);
        final ToggleButton toggleEndAmPm = (ToggleButton) findViewById(R.id.toggleEndAmPm);
        Button createQr = (Button) findViewById(R.id.createQrButton);

        createQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] rawData = new String[] {
                        editName.getText().toString(),
                        editDate.getText().toString(),
                        editDescription.getText().toString(),
                        editStartTime.getText().toString(),
                        toggleStartAmPm.getText().toString(),
                        editEndTime.getText().toString(),
                        toggleEndAmPm.getText().toString()
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
}
