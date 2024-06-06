package kr.ac.cu.moai.dcumusicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.Objects;

public class PlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;
    private Button btnPlayPause;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //int resId = getResources().getIdentifier("vivalavida", "raw", getPackageName());
        int resId = R.raw.test;


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ImageView ivCover = findViewById(R.id.ivCover);
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvArtist = findViewById(R.id.tvArtist);
        TextView tvDuration = findViewById(R.id.tvDuration);
        seekBar = findViewById(R.id.seekBar);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        Button btnNext = findViewById(R.id.btnNext);
        Button btnPrevious = findViewById(R.id.btnPrevious);

        Intent intent = getIntent();
        String mp3file = intent.getStringExtra("mp3");
        MediaPlayer mediaPlayer = MediaPlayer.create(this, resId);

        mediaPlayer.start();

        try (MediaMetadataRetriever retriever = new MediaMetadataRetriever()) {
            retriever.setDataSource(mp3file);
            byte[] b = retriever.getEmbeddedPicture();
            Bitmap cover = BitmapFactory.decodeByteArray(b, 0, b.length);
            ivCover.setImageBitmap(cover);

            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            tvTitle.setText(title);

            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            tvArtist.setText(artist);

            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            tvDuration.setText(getFormattedDuration(duration));

            initializeMediaPlayer(mp3file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        btnPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlayPause.setText("Play");
            } else {
                mediaPlayer.start();
                btnPlayPause.setText("Pause");
                updateSeekBar();
            }
        });

        btnNext.setOnClickListener(v -> {
            // Implement next track functionality
        });

        btnPrevious.setOnClickListener(v -> {
            // Implement previous track functionality
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initializeMediaPlayer(String mp3file) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(mp3file);
            mediaPlayer.prepare();
            seekBar.setMax(mediaPlayer.getDuration());

            mediaPlayer.setOnCompletionListener(mp -> {
                btnPlayPause.setText("Play");
                seekBar.setProgress(0);
            });

            updateSeekBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (mediaPlayer.isPlaying()) {
            updateSeekBar = this::updateSeekBar;
            handler.postDelayed(updateSeekBar, 1000);
        }
    }

    private String getFormattedDuration(String duration) {
        long dur = Long.parseLong(duration);
        long minutes = (dur / 1000) / 60;
        long seconds = (dur / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
        super.onDestroy();
    }
}
