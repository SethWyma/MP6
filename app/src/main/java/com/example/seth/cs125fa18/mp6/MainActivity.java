package com.example.seth.cs125fa18.mp6;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static int REQUEST_CAPTURE = 1;
    ImageView qrImage;
    Context context;
    Uri imageUri;
    File imageFile;
    TextView eventInfo;
    IntentIntegrator qrScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button generateQR = findViewById(R.id.generateQR);
        Button scanQR = findViewById(R.id.scanQR);
        qrImage = findViewById(R.id.qrImage);
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
                        .setOrientationLocked(false);
                qrScanner.initiateScan();
            }
        });

            /**
            Intent cameraLaunch = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            @Override
            public void onClick(View v) {
                try {
                    imageFile = File.createTempFile("JPEG_"
                            + new SimpleDateFormat("yyMMdd-HHmmssSSS", Locale.ENGLISH).format(new Date()) + "_"
                            ,".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                } catch(IOException e) {
                    e.printStackTrace();
                }
                if (imageFile != null) {
                    imageUri = FileProvider.getUriForFile(context, "com.example.seth.cs125fa18.mp6.fileprovider", imageFile);
                    cameraLaunch.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(cameraLaunch, REQUEST_CAPTURE);
                }
            }
        }); */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
        if (requestCode == REQUEST_CAPTURE && resultCode == RESULT_OK) {
            Bitmap qrBmp = null;
            try {
                qrBmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch(IOException e) {
                e.printStackTrace();
            }
            qrImage.setImageURI(imageUri);
            googleQrDecode();
        }
         */
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

                // Parse my very special custom event info format (yay)
                String[] qrArrayData = contents.split(",");
                eventInfo.setText(contents);
            } else {
                Toast.makeText(this, "Scan canceled", Toast.LENGTH_LONG);
            }
        }
    }

    private void googleQrDecode() {
        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                        .build();
        FirebaseVisionImage image = null;
        try {
            image = FirebaseVisionImage.fromFilePath(context, imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
        Task<List<FirebaseVisionBarcode>> scannedCode = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        if (firebaseVisionBarcodes.size() > 1) {
                            eventInfo.setText("Multiple codes found!");
                        } else if (firebaseVisionBarcodes.size() == 1){
                            String qrDataString = firebaseVisionBarcodes.get(0).getRawValue();
                            eventInfo.setText(qrDataString);
                        } else {
                            eventInfo.setText("No codes found.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        eventInfo.setText("No code found.");
                    }
                });
    }
}
