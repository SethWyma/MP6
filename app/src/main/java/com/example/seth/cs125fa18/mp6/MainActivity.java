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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;



import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Collections;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    static int REQUEST_CAPTURE = 1;
    private static final String APPLICATION_NAME = "Google Calendar API";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    //ImageView qrImage;
    Context context;
    Uri imageUri;
    //File imageFile;
    TextView eventInfo;
    IntentIntegrator qrScanner;
    private String[] finalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.NoActionBar);

        Button generateQR = findViewById(R.id.generateQR);
        Button scanQR = findViewById(R.id.scanQR);
        Button addEvent = findViewById(R.id.addEvent);
        Button testButton = findViewById(R.id.testButton);
        // qrImage = findViewById(R.id.qrImage);
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

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalData = new String[5];
                finalData[0] = "Event Name";
                finalData[1] = "2018-12-09T10:00:00+1:00";
                finalData[2] = "2018-12-09T11:00:00+1:00";
                if (finalData != null) {
                    try {
                        addEventToCalendar();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    eventInfo.append("No event has been created yet!");
                }
            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalData != null) {
                    try {
                        addEventToCalendar();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    eventInfo.append("No event has been created yet!");
                }
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
                String[] qrArrayData = contents.split(Character.toString((char) 31));
                qrDataDisplayBuilder(qrArrayData);
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
        finalData = qrArrayData;
        SimpleDateFormat isoParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        Date startDate;
        Date endDate;
        /*String startDateString;
        String endDateString;
        */
        try {
            startDate = isoParser.parse(qrArrayData[1]);
            endDate = isoParser.parse(qrArrayData[2]);
            /*startDateString = isoParser.format(startDate);
            endDateString = isoParser.format(endDate);
            */
        } catch (ParseException e) {
            e.printStackTrace();
            eventInfo.append("There was an error");
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

        if (qrArrayData[3].length() > 0) {
            eventInfo.append("Location: " + qrArrayData[3] + "\n");
        }
        if (qrArrayData[4].length() > 0) {
            eventInfo.append("Description: " + qrArrayData[4] + "\n");
        }
        /*final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar calendar = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        Event newEvent = new Event();
        newEvent.setSummary(qrArrayData[0]);
        if (qrArrayData[3].length() > 0) {
            newEvent.setLocation(qrArrayData[3]);
        }
        if (qrArrayData[4].length() > 0) {
            newEvent.setLocation(qrArrayData[4]);
        }
        DateTime startDateTime = new DateTime(startDateString);
        DateTime endDateTime = new DateTime(endDateString);

        TimeZone currentTimeZone = TimeZone.getDefault();
        String timeZoneAsString = currentTimeZone.getDisplayName();
        EventDateTime startEventDateTime = new EventDateTime();
        startEventDateTime.setDateTime(startDateTime);
        startEventDateTime.setTimeZone(timeZoneAsString);
        newEvent.setStart(startEventDateTime);

        EventDateTime endEventDateTime = new EventDateTime();
        endEventDateTime.setDateTime(endDateTime);
        endEventDateTime.setTimeZone(timeZoneAsString);
        newEvent.setEnd(endEventDateTime);

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("popup").setMinutes(30)
        };
        Event.Reminders reminders = new Event.Reminders();
        reminders.setUseDefault(false);
        reminders.setOverrides(Arrays.asList(reminderOverrides));
        newEvent.setReminders(reminders);
        String id = "primary";
        newEvent = calendar.events().insert(id, newEvent).execute();
        */
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = MainActivity.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

    }

    private void addEventToCalendar() throws IOException, GeneralSecurityException {
        SimpleDateFormat isoParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
        Date startDate;
        Date endDate;
        String startDateString;
        String endDateString;
        try {
            startDate = isoParser.parse(finalData[1]);
            endDate = isoParser.parse(finalData[2]);
            startDateString = isoParser.format(startDate);
            endDateString = isoParser.format(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            eventInfo.append("There was an error");
            return;
        }

        eventInfo.append("Things are happening");
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar calendar = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        Event newEvent = new Event();
        newEvent.setSummary(finalData[0]);
        if (finalData[3].length() > 0) {
            newEvent.setLocation(finalData[3]);
        }
        if (finalData[4].length() > 0) {
            newEvent.setLocation(finalData[4]);
        }
        DateTime startDateTime = new DateTime(startDateString);
        DateTime endDateTime = new DateTime(endDateString);

        TimeZone currentTimeZone = TimeZone.getDefault();
        String timeZoneAsString = currentTimeZone.getDisplayName();
        EventDateTime startEventDateTime = new EventDateTime();
        startEventDateTime.setDateTime(startDateTime);
        startEventDateTime.setTimeZone(timeZoneAsString);
        newEvent.setStart(startEventDateTime);

        EventDateTime endEventDateTime = new EventDateTime();
        endEventDateTime.setDateTime(endDateTime);
        endEventDateTime.setTimeZone(timeZoneAsString);
        newEvent.setEnd(endEventDateTime);

        String id = "primary";
        newEvent = calendar.events().insert(id, newEvent).execute();
        eventInfo.append("The event has been added to your Calendar!");
    }
}
