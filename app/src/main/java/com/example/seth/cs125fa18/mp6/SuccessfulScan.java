package com.example.seth.cs125fa18.mp6;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SuccessfulScan extends AppCompatActivity {
    final int SIGN_IN = 17;
    Button googleSignIn;
    TextView accountName;

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
        googleSignIn = findViewById(R.id.googleSignIn);
        accountName = findViewById(R.id.accountName);
        accountName.setVisibility(View.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        String[] friendlyTextArray = (extras.getStringArray("friendly text"));
        String[] qrData = extras.getStringArray("qr data");

        title.setText(friendlyTextArray[0]);
        firstDatetime.setText(friendlyTextArray[1] + "\n" + friendlyTextArray[2]);
        //secondDatetime.setText(friendlyTextArray[2]);
        location.setText(friendlyTextArray[3]);
        description.setText(friendlyTextArray[4]);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        final GoogleSignInClient signInClient = GoogleSignIn.getClient(this, options);

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleSignInIntent = signInClient.getSignInIntent();
                startActivityForResult(googleSignInIntent, SIGN_IN);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount userAccount = task.getResult(ApiException.class);
                googleSignIn.setVisibility(View.INVISIBLE);
                accountName.setText("Signed in as: " + userAccount.getDisplayName());
                accountName.setVisibility(View.VISIBLE);
            } catch (ApiException e) {
                Log.w("oopsies", "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }
}
