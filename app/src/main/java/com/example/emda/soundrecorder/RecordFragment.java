package com.example.emda.soundrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.io.File;

public class RecordFragment extends Fragment {

    private static final String ARG_POSITION = "fragmentValue";
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();

    private int fragmentValue;

    private Button mRecordButton = null;

    private TextView mRecordingPrompt;
    private int mRecordPromptCount = 0;

    private boolean mStartRecording = true;

    private Chronometer mChronometer = null;


    public static RecordFragment init(int position) {
        RecordFragment recordFragment = new RecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        recordFragment.setArguments(bundle);

        return recordFragment;
    }

    public RecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentValue = getArguments() != null ? getArguments(). getInt(ARG_POSITION) : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);

        mChronometer = (Chronometer) recordView.findViewById(R.id.chronometer);
        mRecordingPrompt = (TextView) recordView.findViewById(R.id.recording_status_text);

        mRecordButton =  recordView.findViewById(R.id.btnRecord);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });


        return recordView;
    }

    // Recording Start/Stop
    //TODO: recording pause
    private void onRecord(boolean start){

        Intent intent = new Intent(getActivity(), RecordingService.class);

        if (start) {
            // start recording
            mRecordButton.setBackgroundResource(R.drawable.stop_recording);

            Toast.makeText(getActivity(), R.string.recording_started, Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/Voice Recorder");
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder


                if (folder.mkdir())
                {
                    Log.i("CreateDir","App dir created");
                }
                else
                {
                    Log.w("CreateDir","Unable to create app dir!");
                }
            }
            else
            {
                Log.i("CreateDir","App dir already exists");
            }



            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 0) {
                        mRecordingPrompt.setText(getString(R.string.recording) + ".");
                    } else if (mRecordPromptCount == 1) {
                        mRecordingPrompt.setText(getString(R.string.recording) + "..");
                    } else if (mRecordPromptCount == 2) {
                        mRecordingPrompt.setText(getString(R.string.recording) + "...");
                        mRecordPromptCount = -1;
                    }

                    mRecordPromptCount++;
                }
            });

            getActivity().startService(intent);
            //to keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingPrompt.setText(getString(R.string.recording) + ".");
            mRecordPromptCount++;

        } else {
            //stop recording
            mRecordButton.setBackgroundResource(R.drawable.ic_launcher);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mRecordingPrompt.setText(R.string.click_to_start_recording);

            getActivity().stopService(intent);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }


}