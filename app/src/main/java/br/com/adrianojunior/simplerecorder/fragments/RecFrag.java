package br.com.adrianojunior.simplerecorder.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.Date;

import br.com.adrianojunior.simplerecorder.R;
import livroandroid.lib.utils.AlertUtils;
import livroandroid.lib.utils.PermissionUtils;

public class RecFrag extends Fragment implements View.OnClickListener {

    private ImageButton trackListButton;

    private NavController controller;

    private ImageButton recButton;

    private boolean recording = false;

    private MediaRecorder recorder;

    private String file;

    private Chronometer chronometer;

    private TextView headerText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rec, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = Navigation.findNavController(view);
        trackListButton = view.findViewById(R.id.list_btn);

        recButton = view.findViewById(R.id.record_btn);

        recButton.setOnClickListener(this);

        trackListButton.setOnClickListener(this);

        chronometer = view.findViewById(R.id.record_timer);

        headerText = view.findViewById(R.id.header_name_text);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            // Navigate to TrackListFrag

            case R.id.list_btn:
                controller.navigate(R.id.action_recFrag_to_tracksListFrag);
                break;

                // Check if audio is recording
                // then start/stop depending on based on it
            case R.id.record_btn:
                if (recording) {

                    // finishes recording the audio
                    // and save the file

                    stopRec();

                } else {

                    // Cehck permissions and
                    // start recording the audio

                    if (checkPermissions()) {

                        startRec();

                    }

                }
                break;
        }
    }

    private void stopRec() {

        recorder.stop();
        recorder.release();
        recorder = null;
        chronometer.stop();
        chronometer.setText("00:00");

        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Gravação")
                .setMessage("O áudio " + file + " foi salvo!")
                .setPositiveButton("OK", null)
                .show();

        headerText.setText(getResources().getString(R.string.start_record_text));

        recording = false;

        recButton.setImageDrawable(getResources()
                .getDrawable(R.drawable.record_btn_stopped));

        trackListButton.setEnabled(true);

    }

    private void startRec() {

        recording = true;

        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();

        Date currentTime = new Date();

        file = android.text.format.DateFormat.format("dd_MM_yyyy_HH_mm_ss", currentTime).toString() + ".3gp";


        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(path + "/" + file);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        recorder.start();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        headerText.setText(getResources().getString(R.string.recording_txt) + file);


        recButton.setImageDrawable(getResources()
                .getDrawable(R.drawable.record_btn_recording));

        trackListButton.setEnabled(false);

    }


    // Checks necessary permissions before recording audio
    private boolean checkPermissions() {


        // Permissions list

        String permissions[] = new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        // Validate manifest permissions

        boolean ok = PermissionUtils.validate(getActivity(), 0, permissions);

        return ok;
    }


    @Override
    public void onStop() {
        super.onStop();

        if (recording)
        stopRec();
    }
}
