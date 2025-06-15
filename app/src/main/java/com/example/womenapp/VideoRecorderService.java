// VideoRecorderService.java
package com.example.womenapp;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class VideoRecorderService extends Service implements SurfaceHolder.Callback {

    private Camera camera;
    private MediaRecorder recorder;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            surfaceView = new SurfaceView(this);
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.addView(surfaceView, new WindowManager.LayoutParams(1, 1));

            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to start video recording", Toast.LENGTH_SHORT).show();
        }
        return START_STICKY;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            File dir = new File(getExternalFilesDir(null), "WomenSafety");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "video_" + new Date().getTime() + ".mp4");

            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            camera.unlock();

            recorder = new MediaRecorder();
            recorder.setCamera(camera);
            recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
            recorder.setOutputFile(file.getAbsolutePath());
            recorder.setPreviewDisplay(holder.getSurface());

            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopRecording();
    }

    private void stopRecording() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
            if (camera != null) {
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
        if (surfaceView != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(surfaceView);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
