package com.example.demo_muzicapp;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageButton btnPlay, btnNext, btnPrevious;
    private SeekBar seekBar;

    private TextView txtCurrentTime, txtTotalTime;

    private Handler handler = new Handler();

    private ArrayList<String> songList = new ArrayList<>();
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        TextView title = findViewById(R.id.songTitle);
        txtCurrentTime = findViewById(R.id.txtCurrentTime);
        txtTotalTime = findViewById(R.id.txtTotalTime);

        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        seekBar = findViewById(R.id.seekBar);

        String titleStr = getIntent().getStringExtra("title");
        String path = getIntent().getStringExtra("file");

        // 🔥 chống crash
        if (path == null || path.isEmpty()) {
            finish();
            return;
        }

        title.setText(titleStr != null ? titleStr : "Unknown");

        songList.add(path);

        playSong(songList.get(currentIndex));

        // PLAY / PAUSE
        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer == null) return;

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
            } else {
                mediaPlayer.start();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
            }
        });

        // NEXT
        btnNext.setOnClickListener(v -> nextSong());

        // PREVIOUS
        btnPrevious.setOnClickListener(v -> prevSong());

        // SEEK
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // 🔥 PLAY SONG
    private void playSong(String path) {
        try {
            if (mediaPlayer != null) {
                handler.removeCallbacks(updateSeekBar);
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, Uri.parse(path));

            mediaPlayer.setOnPreparedListener(mp -> {
                seekBar.setMax(mp.getDuration());

                txtTotalTime.setText(formatTime(mp.getDuration()));

                mp.start();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);

                handler.post(updateSeekBar);
            });

            mediaPlayer.setOnCompletionListener(mp -> nextSong());

            mediaPlayer.prepareAsync(); // 🔥 không lag UI

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // NEXT SONG
    private void nextSong() {
        currentIndex++;
        if (currentIndex >= songList.size()) currentIndex = 0;
        playSong(songList.get(currentIndex));
    }

    // PREVIOUS SONG
    private void prevSong() {
        currentIndex--;
        if (currentIndex < 0) currentIndex = songList.size() - 1;
        playSong(songList.get(currentIndex));
    }

    // UPDATE SEEKBAR + TIME
    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {
                int current = mediaPlayer.getCurrentPosition();

                seekBar.setProgress(current);
                txtCurrentTime.setText(formatTime(current));

                handler.postDelayed(this, 500);
            }
        }
    };

    // FORMAT TIME
    private String formatTime(int ms) {
        int sec = ms / 1000;
        int min = sec / 60;
        sec = sec % 60;
        return String.format("%02d:%02d", min, sec);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            handler.removeCallbacks(updateSeekBar);
            mediaPlayer.release();
        }
    }
}