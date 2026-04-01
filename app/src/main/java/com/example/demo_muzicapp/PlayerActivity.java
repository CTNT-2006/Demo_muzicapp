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
import android.view.animation.LinearInterpolator;
import android.animation.ObjectAnimator;
import android.widget.ImageView;

public class PlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageButton btnPlay, btnNext, btnPrevious;
    private SeekBar seekBar;
    private TextView txtCurrentTime, txtTotalTime;

    private ImageView imgDisc; // 💿 ảnh xoay

    private Handler handler = new Handler();

    private ArrayList<String> songList = new ArrayList<>();
    private int currentIndex = 0;

    private ObjectAnimator rotateAnim; // 💿 animation xoay

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
        imgDisc = findViewById(R.id.imgDisc);

        String titleStr = getIntent().getStringExtra("title");
        String path = getIntent().getStringExtra("file");

        if (path == null || path.isEmpty()) {
            finish();
            return;
        }

        title.setText(titleStr != null ? titleStr : "Unknown");

        songList.add(path);

        // 💿 setup animation xoay
        rotateAnim = ObjectAnimator.ofFloat(imgDisc, "rotation", 0f, 360f);
        rotateAnim.setDuration(10000); // 10s quay 1 vòng
        rotateAnim.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnim.setInterpolator(new LinearInterpolator());

        playSong(songList.get(currentIndex));

        // PLAY / PAUSE
        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer == null) return;

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
                rotateAnim.pause(); // 💿 dừng xoay
            } else {
                mediaPlayer.start();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
                rotateAnim.resume(); // 💿 xoay tiếp
            }
        });

        btnNext.setOnClickListener(v -> nextSong());
        btnPrevious.setOnClickListener(v -> prevSong());

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

                // 💿 bắt đầu xoay
                rotateAnim.start();

                handler.post(updateSeekBar);
            });

            mediaPlayer.setOnCompletionListener(mp -> nextSong());

            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nextSong() {
        currentIndex = (currentIndex + 1) % songList.size();
        playSong(songList.get(currentIndex));
    }

    private void prevSong() {
        currentIndex = (currentIndex - 1 < 0)
                ? songList.size() - 1
                : currentIndex - 1;
        playSong(songList.get(currentIndex));
    }

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