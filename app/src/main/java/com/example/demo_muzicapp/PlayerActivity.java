package com.example.demo_muzicapp;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageButton btnPlay, btnNext, btnPrevious, btnBack;
    private SeekBar seekBar;
    private TextView txtCurrentTime, txtTotalTime, title;
    private ImageView imgDisc, imgSong;
    private Handler handler = new Handler();
    private ArrayList<String> songList = new ArrayList<>();
    private int currentIndex = 0;
    private ObjectAnimator rotateDisc, rotateSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // ===== ÁNH XẠ =====
        title = findViewById(R.id.songTitle);
        txtCurrentTime = findViewById(R.id.txtCurrentTime);
        txtTotalTime = findViewById(R.id.txtTotalTime);

        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnBack = findViewById(R.id.btnBack);

        seekBar = findViewById(R.id.seekBar);
        imgDisc = findViewById(R.id.imgDisc);
        imgSong = findViewById(R.id.imgSong);

        // ===== NHẬN DATA =====
        ArrayList<String> list = getIntent().getStringArrayListExtra("list");
        currentIndex = getIntent().getIntExtra("index", 0);

        if (list != null && list.size() > 0) {
            songList = list;
        } else {
            String path = getIntent().getStringExtra("file");
            if (path != null) songList.add(path);
        }

        if (songList.size() == 0) {
            finish();
            return;
        }

        // ===== ANIMATION =====
        rotateDisc = ObjectAnimator.ofFloat(imgDisc, "rotation", 0f, 360f);
        rotateDisc.setDuration(10000);
        rotateDisc.setRepeatCount(ObjectAnimator.INFINITE);
        rotateDisc.setInterpolator(new LinearInterpolator());

        rotateSong = ObjectAnimator.ofFloat(imgSong, "rotation", 0f, 360f);
        rotateSong.setDuration(10000);
        rotateSong.setRepeatCount(ObjectAnimator.INFINITE);
        rotateSong.setInterpolator(new LinearInterpolator());

        // ===== PLAY NGAY =====
        playSong(songList.get(currentIndex));

        // ===== PLAY / PAUSE =====
        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer == null) return;

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
                pauseAnimation();
            } else {
                mediaPlayer.start();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                resumeAnimation();
            }
        });

        // ===== NEXT / PREV =====
        btnNext.setOnClickListener(v -> nextSong());
        btnPrevious.setOnClickListener(v -> prevSong());

        // ===== BACK =====
        btnBack.setOnClickListener(v -> finish());

        // ===== SEEK BAR =====
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

    // ================= PLAY SONG =================
    private void playSong(String path) {
        try {
            // Release MediaPlayer cũ nếu có
            if (mediaPlayer != null) {
                handler.removeCallbacks(updateSeekBar);
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            Uri uri = Uri.parse(path);
            mediaPlayer = MediaPlayer.create(this, uri);

            if (mediaPlayer == null) {
                // Không play được
                return;
            }

            // Cập nhật UI
            title.setText(getFileName(path));
            seekBar.setMax(mediaPlayer.getDuration());
            txtTotalTime.setText(formatTime(mediaPlayer.getDuration()));

            mediaPlayer.start();
            btnPlay.setImageResource(android.R.drawable.ic_media_pause);

            startAnimation();
            handler.post(updateSeekBar);

            mediaPlayer.setOnCompletionListener(mp -> nextSong());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= NEXT =================
    private void nextSong() {
        if (songList.size() == 0) return;
        currentIndex = (currentIndex + 1) % songList.size();
        playSong(songList.get(currentIndex));
    }

    // ================= PREVIOUS =================
    private void prevSong() {
        if (songList.size() == 0) return;
        currentIndex = (currentIndex - 1 + songList.size()) % songList.size();
        playSong(songList.get(currentIndex));
    }

    // ================= ANIMATION =================
    private void startAnimation() {
        rotateDisc.start();
        rotateSong.start();
    }

    private void pauseAnimation() {
        if (rotateDisc.isRunning()) rotateDisc.pause();
        if (rotateSong.isRunning()) rotateSong.pause();
    }

    private void resumeAnimation() {
        rotateDisc.resume();
        rotateSong.resume();
    }

    // ================= SEEK UPDATE =================
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

    // ================= FORMAT TIME =================
    private String formatTime(int ms) {
        int sec = ms / 1000;
        int min = sec / 60;
        sec = sec % 60;
        return String.format("%02d:%02d", min, sec);
    }

    // ================= HELPER =================
    private String getFileName(String path) {
        if (path == null) return "";
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1) return path;
        return path.substring(lastSlash + 1);
    }

    // ================= DESTROY =================
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            handler.removeCallbacks(updateSeekBar);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}