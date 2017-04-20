package com.example.terry.strat_rpg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RatingBar;
import android.widget.ImageView;
import android.widget.Button;
import static com.example.terry.strat_rpg.R.id.musicDecreaseVolume;

/**
 * OptionsPopUp provides the user with the ability to adjust sound and music volume levels.
 * The levels are then passed back to MainActivity, which can then be passed along to any other
 * class that will require them.  There are multiple ways of doing this, but for now we are using
 * resource bundles within intents to pass the values.
 */
public class OptionsPopUp extends Activity implements View.OnClickListener {

    public RatingBar musicVolumeRatingBar, soundVolumeRatingBar;
    public MediaPlayer mpMusic, mpSound;
    public float intSoundVolume, intMusicVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.options_pop_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Prevents the popup from being closeable when the user clicks outside of the dialog
        setFinishOnTouchOutside(false);

        // Gets rid of the white corners around the edges
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Scales size of window to be smaller
        getWindow().setLayout((int) (width*.7), (int) (height*.7));

        Intent i = getIntent();
        Bundle b = i.getExtras();
        intMusicVolume = (float)b.get("intMusicVolume");
        intSoundVolume = (float)b.get("intSoundVolume");

        Button mainMenuButton = (Button) findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mpMusic = MainActivity.mpOpening;
        mpSound = MainActivity.mpSound;

        // The rating bars here will correspond with volume control for music and sound
        musicVolumeRatingBar = (RatingBar) findViewById(R.id.musicVolumeRatingBar);
        musicVolumeRatingBar.setRating(intMusicVolume);

        soundVolumeRatingBar = (RatingBar) findViewById(R.id.soundVolumeRatingBar);
        soundVolumeRatingBar.setRating(intSoundVolume);

        // Initializes increase & decrease buttons for sound and music levels
        ImageView musicIncreaseVolume = (ImageView) findViewById(R.id.musicIncreaseVolume);
        musicIncreaseVolume.setOnClickListener(this);
        ImageView musicDecreaseVolume = (ImageView) findViewById(R.id.musicDecreaseVolume);
        musicDecreaseVolume.setOnClickListener(this);
        ImageView soundIncreaseVolume = (ImageView) findViewById(R.id.soundIncreaseVolume);
        soundIncreaseVolume.setOnClickListener(this);
        ImageView soundDecreaseVolume = (ImageView) findViewById(R.id.soundDecreaseVolume);
        soundDecreaseVolume.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent();
        i.putExtra("intMusicVolume", intMusicVolume);
        i.putExtra("intSoundVolume", intSoundVolume);
        setResult(RESULT_OK, i);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        int maxVolume = 9;

        float soundCurrentVolume = soundVolumeRatingBar.getRating();
        float log2=(float)(Math.log(maxVolume-soundCurrentVolume)/Math.log(maxVolume));
        mpSound.setVolume(1-log2, 1-log2);

        float musicCurrentVolume = musicVolumeRatingBar.getRating();
        float log1=(float)(Math.log(maxVolume-musicCurrentVolume)/Math.log(maxVolume));
        mpMusic.setVolume(1-log1, 1-log1);

        switch (view.getId()){
            case R.id.soundIncreaseVolume:{
                float x = soundVolumeRatingBar.getRating();
                if (x != 10){
                    x = x + 1;
                }
                soundVolumeRatingBar.setRating(x);
                mpSound.start();
                break;
            }
            case R.id.soundDecreaseVolume:{
                float x = soundVolumeRatingBar.getRating();
                if (x != 0){
                    x = x - 1;
                }
                soundVolumeRatingBar.setRating(x);
                mpSound.start();
                break;
            }
            case R.id.musicIncreaseVolume:{
                float x = musicVolumeRatingBar.getRating();
                if (x != 10){
                    x = x + 1;
                }
                musicVolumeRatingBar.setRating(x);
                break;
            }
            case musicDecreaseVolume:{
                float x = musicVolumeRatingBar.getRating();
                if (x != 0){
                    x = x - 1;
                }
                musicVolumeRatingBar.setRating(x);
                break;
            }
        }
        intMusicVolume = musicVolumeRatingBar.getRating();
        intSoundVolume = soundVolumeRatingBar.getRating();
    }
}
