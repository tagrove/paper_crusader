package com.example.terry.strat_rpg;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Timer;

/**
 * @name Terry Grové
 * @name2 Stephen Penton
 *
 * @email tagrove@g.coastal.edu
 * @email2 sepenton@g.coastal.edu
 *
 * @version 1.0
 * @date 4/20/2017
 *
 * Main class for Paper Crusader, application designed for CSCI 343 - Mobile Application Development
 * Final Project.
 *
 * This is a game designed for android devices, minimum version KitKat for all of the current features.
 * Future works would be implementing work arounds so that the minimum SDK version covers more devices.
 *
 * TODO: Decide how exactly to initialize the sound variables.  I'm not sure if they need to be public.
 * TODO: Add "Credits" area so that open source assets may be correctly attributed.
 * TODO: Try to get assets at some point that are completely royalty free - not just googled images
 * Attributions: In the credits, please place
 * "Some of the sounds in this project were created by ViRiX Dreamcore (David McKee) soundcloud.com/virix"
 */
public class MainActivity extends AppCompatActivity {

    private String COMPANY_NAME = "iNeedADegree Games";
    private String MY_APP_VERSION = "version: there are bugs";
    public static MediaPlayer mpOpening, mpSound;
    private boolean soundIsLoaded = false;
    private boolean loading = false;
    private final int MAX_VOLUME = 9;
    private float musicVolume = 5;
    private float soundVolume = 5;
    public static SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = getWindow().getDecorView();

        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);
            }
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        //noinspection deprecation
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<>();
        soundPoolMap.put(1, soundPool.load(this, R.raw.crusader_menu_confirm, 1));
        soundPoolMap.put(2, soundPool.load(this, R.raw.crusader_menu_cancel, 1));
        soundPoolMap.put(3, soundPool.load(this, R.raw.crusader_opening_theme, 1));
        soundPoolMap.put(4, soundPool.load(this, R.raw.crusader_begin_new_save, 1));

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
                soundIsLoaded = true;
            }
        });

        mpSound = MediaPlayer.create(this, R.raw.crusader_menu_confirm);
        mpOpening = MediaPlayer.create(this, R.raw.crusader_opening_theme);
        // Ensures that the music and sound begin at 50% volume.
        musicVolume = (float)(Math.log(MAX_VOLUME - 5)/Math.log(MAX_VOLUME));
        mpOpening.setVolume(1-musicVolume, 1-musicVolume);

        soundVolume = (float)(Math.log(MAX_VOLUME - 5)/Math.log(MAX_VOLUME));
        mpSound.setVolume(1-soundVolume, 1-soundVolume);
        mpOpening.start();
        soundVolume = 5f;
        musicVolume = 5f;

        Intent previousIntent = getIntent();
        Bundle b = previousIntent.getExtras();
        if (b != null){
            soundVolume = b.getFloat("intSoundVolume");
            musicVolume = b.getFloat("intMusicVolume");
        }

        // Initializes the rating bars corresponding to the sound levels at 50% (5 bars)
        TextView companyTextView = (TextView) findViewById(R.id.companyTextView);
        TextView versionTextView = (TextView) findViewById(R.id.versionTextView);

        // Set variables for company in bottom left and version in bottom right
        companyTextView.setText(COMPANY_NAME);
        versionTextView.setText(MY_APP_VERSION);

        // Set up the background so that it has two images in order to have the
        // infinite scrolling background.
        final ImageView backgroundOne = (ImageView) findViewById(R.id.background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two);
        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, -1.0f);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(120000L);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX + width);
            }
        });

        // Set up the button / listener for the New Game Button
        Button newGameButton = (Button) findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                // Loading checks to ensure that a save slot has not already been selected
                // and the activity is getting ready to launch
                if (!loading) {
                    v.startAnimation(animAlpha);
                    soundPool.play(1, soundVolume / 10, soundVolume / 10, 1, 0, 1f);
                    Intent i = new Intent(MainActivity.this, CreateAndLoadGamePopUp.class);
                    i.putExtra("intMusicVolume", musicVolume);
                    i.putExtra("intSoundVolume", soundVolume);
                    i.putExtra("new", "new");

                    // Code 222 = Create New Game
                    startActivityForResult(i, 222);
                }
            }
        });

        // Set up the button / listener for the Load Game Button
        Button loadGameButton = (Button) findViewById(R.id.loadGameButton);
        loadGameButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){

                // Loading checks to ensure that a save slot has not already been selected
                // and the activity is getting ready to launch
                if (!loading) {
                    v.startAnimation(animAlpha);
                    soundPool.play(1, soundVolume / 10, soundVolume / 10, 1, 0, 1f);
                    Intent i = new Intent(MainActivity.this, CreateAndLoadGamePopUp.class);
                    i.putExtra("intMusicVolume", musicVolume);
                    i.putExtra("intSoundVolume", soundVolume);
                    i.putExtra("load", "load");

                    // Code 111 = Load Existing Game
                    startActivityForResult(i, 111);
                }
            }
        });

        // Set up the button / listener for the Options Button
        Button optionsButton = (Button) findViewById(R.id.options_button);
        optionsButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                // Loading checks to ensure that a save slot has not already been selected
                // and the activity is geting ready to launch
                if (!loading) {
                    v.startAnimation(animAlpha);
                    soundPool.play(1, soundVolume / 10, soundVolume / 10, 1, 0, 1f);
                    Intent i = new Intent(MainActivity.this, OptionsPopUp.class);
                    i.putExtra("intMusicVolume", musicVolume);
                    i.putExtra("intSoundVolume", soundVolume);

                    // Code 333 = Options
                    startActivityForResult(i, 333);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null){
            Bundle b = data.getExtras();
            if (requestCode == 333 && resultCode == RESULT_OK){
                soundVolume = (float) b.get("intSoundVolume");
                musicVolume = (float) b.get("intMusicVolume");
            }
            // 111 = LoadGame.  If Boolean confirmStart == true, time to load a new game.
            else if (requestCode == 111 && resultCode == RESULT_OK){
                startGame();
                loading = true;
            }
            // 222 = NewGame.  If Boolean confirmStart == true, time to create a new game.
            else if (requestCode == 222 && resultCode == RESULT_OK){
                startGame();
                loading = true;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }else {
            soundPool.play(2, soundVolume / 10, soundVolume / 10, 1, 0, 1f);
        }
    }

    public void startGame(){
        int runTime = 1000;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                /* Create an intent that will start the next activity. */
                Intent mainIntent = new Intent(MainActivity.this, Game.class);
                mainIntent.putExtra("intMusicVolume", musicVolume);
                mainIntent.putExtra("intSoundVolume", soundVolume);
                Player player = new Player();
                mainIntent.putExtra("player", player);
                startActivity(mainIntent);
                /* Finish activity so user cannot go back to it.
                *  If user needs to return to the main activity, a new activity will be created*/
                MainActivity.this.finish();

                /* Applies slide out / slide in animations */
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
                Timer timer = new Timer(true);

                // TODO - figure out how to make the music actually fade out.
                timer.schedule(new FadeMusicOut(), 0, 200);
                mpOpening.stop();
            }
        }, runTime);
    }


}
