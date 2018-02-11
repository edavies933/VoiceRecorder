package com.example.emda.soundrecorder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by emda on 2/11/2018.
 */

public class RecordingService extends Service {


    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mRecorder = null;

    private DatabaseHelperMethod mDatabase;

    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new DatabaseHelperMethod(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            stopRecording();
        }

        super.onDestroy();
    }

    public void startRecording() {
        setFileNameAndPath();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(192000);

        try {
            mRecorder.prepare();
            mRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();



        } catch (IOException e) {
        }
    }

    public void setFileNameAndPath(){
        int count = 0;
        File file;

        do{
            count++;

            mFileName = "Record"
                    + "_" + (mDatabase.getCount() + count) + ".mp4";
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/Voice Recorder/" + mFileName;

            file = new File(mFilePath);
        }while (file.exists() && !file.isDirectory());
    }

    public void stopRecording() {
        mRecorder.stop();
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder.release();
        Toast.makeText(this, getString(R.string.record_saved_at) + " " + mFilePath, Toast.LENGTH_LONG).show();


        mRecorder = null;

        try {
            mDatabase.addRecord(mFileName, mFilePath, mElapsedMillis);

        } catch (Exception e){
        }
    }



}
