package com.example.emda.soundrecorder;

import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by emda on 2/11/2018.
 */
public class FileViewerFragment extends Fragment {
    private static final String ARG_POSITION = "fragmentValue";

    private int fragmentValue;
    private FilesAdapter mFilesAdapter;

    public static FileViewerFragment newInstance(int position) {
        FileViewerFragment fileViewerFragment = new FileViewerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        fileViewerFragment.setArguments(bundle);

        return fileViewerFragment;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentValue = getArguments() != null ? getArguments(). getInt(ARG_POSITION) : 1;
        observer.startWatching();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_file_viewer, container, false);

        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //reverse linearlayout to show newest to oldest order
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mFilesAdapter = new FilesAdapter(getActivity(), linearLayoutManager);
        mRecyclerView.setAdapter(mFilesAdapter);

        return v;
    }

    FileObserver observer =
            new FileObserver(android.os.Environment.getExternalStorageDirectory().toString()
                    + "/Voice Recorder") {
                // set up a file observer to monitor this directory on sd card
                @Override
                public void onEvent(int event, String file) {
                    if(event == FileObserver.DELETE){



                    }
                }
            };
}




