package com.example.womenapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private EditText phoneNumberEditText;
    private Button sosButton, recordButton, chatBotButton, callButton,btnLiveLocation;
    private static final int REQUEST_PERMISSIONS = 100;

    private Camera camera;
    private MediaRecorder mediaRecorder;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private File videoFile;
    private boolean isRecording = false;

    // Define your favorite phone numbers here
    private final String[] favoriteNumbers = {
            "8999009895(vaishnavi)",
            "7827170170(domestic_violence)",
            "112(EmergencyRespond_Support)",
            "07477722424(MH WC)",
            "100(emergency)",
            "1091(Emergency response)"
    };

    // New: Messages to send with the call
    private final String[] callSafetyMessages = {
            "Please pick up! I need help.",
            "Emergency Call! Answer urgently.",
            "I'm in danger, call me back immediately.",
            "SOS: Call from me."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        phoneNumberEditText = findViewById(R.id.numberEditText);
        sosButton = findViewById(R.id.sosButton);
        recordButton = findViewById(R.id.startRecordingButton);
        chatBotButton = findViewById(R.id.chatButton);
        callButton = findViewById(R.id.callButton);
        btnLiveLocation = findViewById(R.id.btnLiveLocation);
        // Initialize the new call button
        Button periodTrackerButton = findViewById(R.id.periodTrackerButton);
         Button btnLiveLocation=findViewById(R.id.btnLiveLocation);
        periodTrackerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PeriodTrackerActivity.class);
                startActivity(intent);
            }



        });


        surfaceView = findViewById(R.id.cameraPreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        // Make SurfaceView minimal but visible (not GONE) for background recording
        surfaceView.getLayoutParams().width = 1;
        surfaceView.getLayoutParams().height = 1;
        surfaceView.requestLayout();

        // Request all necessary permissions
        requestAllPermissions();

        // --- SOS Button Listener (Existing functionality: send SMS from EditText) ---
        sosButton.setOnClickListener(v -> {
            String number = phoneNumberEditText.getText().toString().trim();
            if (number.isEmpty()) {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] messages = {"Save me", "I need help!", "Call me back!", "I'm in danger"};
            new AlertDialog.Builder(this)
                    .setTitle("Select SOS Message")
                    .setItems(messages, (dialog, which) -> sendSms(number, messages[which]))
                    .show();
        });

        // --- Record Button Listener ---
        recordButton.setOnClickListener(v -> {
            if (!checkPermissions()) {
                Toast.makeText(this, "Permissions not granted. Please allow all requested permissions.", Toast.LENGTH_LONG).show();
                requestAllPermissions();
                return;
            }

            if (!isRecording) {
                if (prepareMediaRecorder()) {
                    try {
                        mediaRecorder.start();
                        isRecording = true;
                        Toast.makeText(this, "Recording started...", Toast.LENGTH_SHORT).show();
                        recordButton.setText("Stop Recording");
                    } catch (IllegalStateException e) {
                        Toast.makeText(this, "Failed to start recording: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        releaseMediaRecorder();
                        releaseCamera();
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    mediaRecorder.stop();
                    Toast.makeText(this, "Recording stopped. Saved to: " + videoFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (RuntimeException stopException) {
                    Toast.makeText(this, "Recording stop failed or was too short to save.", Toast.LENGTH_SHORT).show();
                } finally {
                    releaseMediaRecorder();
                    releaseCamera();
                    isRecording = false;
                    recordButton.setText("🎥 Record");
                    scanFileForGallery(videoFile);
                }
            }
        });

        // --- Call Button Listener (MODIFIED to include SMS logic) ---
        callButton.setOnClickListener(v -> {
            // Check for both CALL_PHONE and SEND_SMS permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Call and SMS permissions are required. Please allow.", Toast.LENGTH_LONG).show();
                // Request both permissions if either is missing
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS}, REQUEST_PERMISSIONS);
            } else {
                // If permissions are granted, show the dialog to select a number and then a message
                showCallNumberAndMessageDialog();
            }
        });

        // --- Chatbot Button Listener ---
        chatBotButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);
            startActivity(intent);
        });

        btnLiveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, livelocation.class));
            }
        });
    }
//--- location tracking---


    private boolean prepareMediaRecorder() {
        try {
            releaseCamera();
            camera = Camera.open();
            if (camera == null) {
                Toast.makeText(this, "Failed to open camera.", Toast.LENGTH_SHORT).show();
                return false;
            }

            camera.setDisplayOrientation(90);
            camera.unlock();

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setCamera(camera);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
            mediaRecorder.setOrientationHint(90);

            videoFile = getOutputMediaFile();
            if (videoFile == null) {
                Toast.makeText(this, "Unable to create output file for video.", Toast.LENGTH_SHORT).show();
                return false;
            }

            mediaRecorder.setOutputFile(videoFile.getAbsolutePath());

            if (surfaceHolder.getSurface() == null || !surfaceHolder.getSurface().isValid()) {
                Toast.makeText(this, "Camera preview surface is not ready.", Toast.LENGTH_SHORT).show();
                return false;
            }

            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

            mediaRecorder.prepare();
            return true;
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(this, "Recorder preparation error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            releaseMediaRecorder();
            releaseCamera();
            return false;
        }
    }

    /**
     * Creates a file in the public pictures directory for storing video recordings.
     * @return The File object for the video, or null if creation fails.
     */
    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "WomenAppVideos");
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Toast.makeText(this, "Failed to create video storage directory.", Toast.LENGTH_SHORT).show();
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
    }

    /**
     * Requests all necessary runtime permissions from the user.
     */
    private void requestAllPermissions() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions = new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CALL_PHONE
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE
            };
        }
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }

    /**
     * Checks if all required permissions are granted.
     * @return true if all permissions are granted, false otherwise.
     */
    private boolean checkPermissions() {
        boolean cameraPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean audioPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        boolean smsPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        boolean callPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
        boolean storagePerm = true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            storagePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return cameraPerm && audioPerm && smsPerm && callPerm && storagePerm;
    }

    /**
     * Sends an SMS message to a specified number.
     * @param numberWithLabel The recipient's phone number string including label (e.g., "8999009895(vaishnavi)").
     * @param message The message to send.
     */
    private void sendSms(String numberWithLabel, String message) {
        // Extract just the number from the string like "8999009895(vaishnavi)"
        String actualNumber = numberWithLabel.split("\\(")[0].trim();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS permission is required to send messages.", Toast.LENGTH_SHORT).show();
            // Request permission again, though it should be handled in onRequestPermissionsResult
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_PERMISSIONS);
        } else {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(actualNumber, null, message, null, null);
                Toast.makeText(this, "Safety message sent to " + actualNumber, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to send SMS to " + actualNumber + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    /**
     * Displays a dialog to let the user select one of the favorite numbers,
     * and then a second dialog to select a safety message.
     */
    private void showCallNumberAndMessageDialog() {
        String[] displayNumbers = new String[favoriteNumbers.length];
        for (int i = 0; i < favoriteNumbers.length; i++) {
            displayNumbers[i] = "Call " + favoriteNumbers[i];
        }

        new AlertDialog.Builder(this)
                .setTitle("Select a Number to Call")
                .setItems(displayNumbers, (dialog, which) -> {
                    String selectedNumberWithLabel = favoriteNumbers[which]; // Store the full string

                    new AlertDialog.Builder(this)
                            .setTitle("Select Safety Message to Send")
                            .setItems(callSafetyMessages, (msgDialog, msgWhich) -> {
                                String selectedMessage = callSafetyMessages[msgWhich];
                                // Pass both the full number string and the selected message
                                initiatePhoneCallAndSms(selectedNumberWithLabel, selectedMessage);
                            })
                            .show();
                })
                .show();
    }

    /**
     * Initiates a phone call and sends an SMS to the given number.
     * Requires CALL_PHONE and SEND_SMS permissions.
     * @param numberWithLabel The full number string (e.g., "8999009895(vaishnavi)").
     * @param message The safety message to send via SMS.
     */
    private void initiatePhoneCallAndSms(String numberWithLabel, String message) {
        // Extract just the number from the string (e.g., "8999009895")
        String actualNumber = numberWithLabel.split("\\(")[0].trim();

        // Final permission check before acting
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Both Call and SMS permissions are required for this action.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Send the SMS message first
        sendSms(numberWithLabel, message); // Re-using sendSms, which handles extracting the number

        // 2. Initiate the phone call
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + actualNumber));
            startActivity(callIntent);
            Toast.makeText(this, "Calling " + actualNumber + "...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to initiate call to " + actualNumber + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    /**
     * Scans the media file to make it visible in the device's gallery.
     * @param file The video file to scan.
     */
    private void scanFileForGallery(File file) {
        if (file != null && file.exists()) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        }
    }

    /**
     * Releases the MediaRecorder resources.
     */
    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    /**
     * Releases the Camera resources.
     */
    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // No specific camera setup needed here as it's done in prepareMediaRecorder
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Not critical for a 1x1 SurfaceView, but generally you'd reconfigure camera preview here
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseMediaRecorder();
        releaseCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRecording) {
            try {
                mediaRecorder.stop();
                Toast.makeText(this, "Recording stopped due to app pause.", Toast.LENGTH_SHORT).show();
            } catch (RuntimeException e) {
                Toast.makeText(this, "Recording stopped due to app pause, but might be too short to save properly.", Toast.LENGTH_SHORT).show();
            }
            scanFileForGallery(videoFile);
        }
        releaseMediaRecorder();
        releaseCamera();
        isRecording = false;
        recordButton.setText("🎥 Record");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaRecorder();
        releaseCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Toast.makeText(this, "All required permissions granted!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Some critical permissions were denied. App features may not work as expected.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
