package com.example.womenapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.media.MediaScannerConnection;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioRecorderService extends Service {
    private MediaRecorder recorder;
    private String filePath;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            // Save to public Music directory so it's visible in gallery
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "WomenSafety");
            if (!dir.exists()) dir.mkdirs();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            filePath = new File(dir, "audio_" + timestamp + ".3gp").getAbsolutePath();

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(filePath);
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            recorder.stop();
            recorder.release();

            // Notify media scanner so file appears in gallery
            MediaScannerConnection.scanFile(this, new String[]{filePath}, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
