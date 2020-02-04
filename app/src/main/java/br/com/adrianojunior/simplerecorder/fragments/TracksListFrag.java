package br.com.adrianojunior.simplerecorder.fragments;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;

import br.com.adrianojunior.simplerecorder.R;
import br.com.adrianojunior.simplerecorder.adapter.TracksAdapter;

public class TracksListFrag extends Fragment implements TracksAdapter.onTrackClick {

    private RecyclerView tracksRecyclerView;

    private TracksAdapter tracksAdapter;

    private  ConstraintLayout playerSheet;

    private BottomSheetBehavior behavior;

    private File[] allFiles;

    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;

    private File filePlay = null;

    private TextView playerName;
    private ImageButton playPauseBtn;
    private TextView headerTitle;

    private SeekBar playerSeekBar;

    private Handler seekBarHandler;
    private Runnable seekBarRunnable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracks_list, container, false);


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        final File directory = new File(path);
        allFiles = directory.listFiles();

        playerSheet = view.findViewById(R.id.player_sheet);
        behavior = BottomSheetBehavior.from(playerSheet);

        playPauseBtn = view.findViewById(R.id.play_track_btn);
        playerName = view.findViewById(R.id.player_header_name);
        headerTitle = view.findViewById(R.id.player_title);

        playerSeekBar = view.findViewById(R.id.media_seekbar);

        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        tracksAdapter = new TracksAdapter(allFiles, this);

        tracksRecyclerView = view.findViewById(R.id.tracks_list_view);

        tracksRecyclerView.setAdapter(tracksAdapter);
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tracksRecyclerView.setHasFixedSize(true);


        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    pauseAudio();
                } else {
                    if (filePlay != null)
                        resumeAudio();
                }
            }
        });

    }

    @Override
    public void onClickListener(File file, int position) {

        filePlay = file;
        if (isPlaying) {
            stopPlaying();
            playAudio(filePlay);
        } else {
            playAudio(filePlay);
        }
    }

    private void playAudio(File playFile) {

        mediaPlayer = new MediaPlayer();

        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        try {

            mediaPlayer.setDataSource(playFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {

            e.printStackTrace();

        }

        playerName.setText(playFile.getName());

        playPauseBtn.setImageDrawable(getActivity()
                .getResources()
                .getDrawable(R.drawable.ic_action_pause));

        headerTitle.setText(getActivity()
                .getResources()
                .getString(R.string.playing));

        isPlaying = true;


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                headerTitle.setText(getActivity()
                        .getResources()
                        .getString(R.string.paused));

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopPlaying();
                    }
                }, 500);

            }
        });

        playerSeekBar.setMax(mediaPlayer.getDuration());

        seekBarHandler = new Handler();
        updateRunnable();
        seekBarHandler.postDelayed(seekBarRunnable, 0);

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (filePlay != null)
                    pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (filePlay != null) {
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }
            }
        });
    }

    private void updateRunnable() {
        seekBarRunnable = new Runnable() {
            @Override
            public void run() {
                playerSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekBarHandler.postDelayed(this, 500);
            }
        };
    }

    private void stopPlaying() {

        mediaPlayer.stop();

        seekBarHandler.removeCallbacks(seekBarRunnable);

        playPauseBtn.setImageDrawable(getActivity()
                .getResources()
                .getDrawable(R.drawable.ic_action_play));

        headerTitle.setText(getActivity()
                .getResources()
                .getString(R.string.now_playing));

        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        isPlaying = false;


    }

    private void pauseAudio() {
        isPlaying = false;

        seekBarHandler.removeCallbacks(seekBarRunnable);

        mediaPlayer.pause();

        headerTitle.setText(getActivity().
                getResources()
                .getString(R.string.paused));

        playPauseBtn.setImageDrawable(getActivity()
                .getResources()
                .getDrawable(R.drawable.ic_action_play));
    }

    private void resumeAudio() {

        isPlaying = true;

        mediaPlayer.start();

        playPauseBtn.setImageDrawable(getActivity()
                .getResources()
                .getDrawable(R.drawable.ic_action_pause));

        updateRunnable();

        seekBarHandler.postDelayed(seekBarRunnable, 0);

        headerTitle.setText(getActivity()
        .getResources()
        .getString(R.string.playing));

    }

    @Override
    public void onStop() {
        super.onStop();
        if (isPlaying)
            stopPlaying();
    }
}
