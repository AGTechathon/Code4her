package com.example.womenapp;

import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PeriodTrackerActivity extends AppCompatActivity {

    private Button selectDateButton;
    private ProgressBar progressBar;
    private TextView progressText;
    private final int PERIOD_CYCLE_DAYS = 28;
    private final int SMS_PERMISSION_REQUEST_CODE = 101;
    private final String TARGET_PHONE_NUMBER = "8329216869"; // Change to actual number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_period_tracker);

        selectDateButton = findViewById(R.id.selectDateButton);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        selectDateButton.setOnClickListener(v -> openDatePicker());

        // Request SMS permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        }
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    handlePeriodDate(calendar);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void handlePeriodDate(Calendar startDate) {
        Calendar today = Calendar.getInstance();
        long diffMillis = today.getTimeInMillis() - startDate.getTimeInMillis();
        int daysPassed = (int) (diffMillis / (1000 * 60 * 60 * 24));

        int daysLeft = PERIOD_CYCLE_DAYS - daysPassed;
        if (daysLeft < 0) daysLeft = 0;
        if (daysPassed < 0) daysPassed = 0;

        progressBar.setProgress(daysPassed);
        progressText.setText("Days Left: " + daysLeft);

        if (daysLeft <= 2) {
            sendReminderSMS(daysLeft);
        }
    }

    private void sendReminderSMS(int daysLeft) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(TARGET_PHONE_NUMBER, null,
                    "Reminder: Your next period may start in " + daysLeft + " day(s).",
                    null, null);
            Toast.makeText(this, "SMS Sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "SMS Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
