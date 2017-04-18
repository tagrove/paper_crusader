package com.example.terry.strat_rpg;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
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
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    String COMPANY_NAME = "iNeedADegree Games";
    final String MY_APP_VERSION = "version: there are bugs";
    public static MediaPlayer mpOpening, mpSound;
    public final int MAX_VOLUME = 9;
    public float musicVolume = 5;
    public float soundVolume = 5;
    public float intMusicVolume = 5;
    public float intSoundVolume = 5;

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
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);


        mpSound = MediaPlayer.create(this, R.raw.crusader_menu_confirm);
        mpOpening = MediaPlayer.create(this, R.raw.crusader_opening_theme);


        musicVolume = (float)(Math.log(MAX_VOLUME - 5)/Math.log(MAX_VOLUME));
        mpOpening.setVolume(1-musicVolume, 1-musicVolume);
        mpOpening.start();

        soundVolume = (float)(Math.log(MAX_VOLUME - 5)/Math.log(MAX_VOLUME));
        mpSound.setVolume(1-soundVolume, 1-soundVolume);

        // Initializes the rating bars corresponding to the sound levels at 50% (5 bars)
        TextView companyTextView = (TextView) findViewById(R.id.companyTextView);
        TextView versionTextView = (TextView) findViewById(R.id.versionTextView);

        companyTextView.setText(COMPANY_NAME);
        versionTextView.setText(MY_APP_VERSION);

        final ImageView backgroundOne = (ImageView) findViewById(R.id.background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two);
        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, -1.0f);

        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.setDuration(120000L);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
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

        Button newGameButton = (Button) findViewById(R.id.newGameButton);
        newGameButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                //startActivity(new Intent(MainActivity.this, Pop.class));
                v.startAnimation(animAlpha);
                mpSound.start();

                Intent i = new Intent(MainActivity.this, LoadSavePopUp.class);
                i.putExtra("intMusicVolume", intMusicVolume);
                i.putExtra("intSoundVolume", intSoundVolume);
                i.putExtra("new", "new");
                // Loading the game
                startActivityForResult(i, 222);
            }
        });

        Button loadGameButton = (Button) findViewById(R.id.loadGameButton);
        loadGameButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                v.startAnimation(animAlpha);
                mpSound.start();
                Intent i = new Intent(MainActivity.this, LoadSavePopUp.class);
                i.putExtra("intMusicVolume", intMusicVolume);
                i.putExtra("intSoundVolume", intSoundVolume);
                i.putExtra("load", "load");
                // Loading the game
                startActivityForResult(i, 111);
            }
        });

        // Attributions: In the credits, please place
        // "Some of the sounds in this project were created by ViRiX Dreamcore (David McKee) soundcloud.com/virix"
        Button optionsButton = (Button) findViewById(R.id.options_button);

        optionsButton.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                v.startAnimation(animAlpha);
                mpSound.start();
                Intent i = new Intent(MainActivity.this, OptionsPopUp.class);
                i.putExtra("options", "options");
                i.putExtra("intMusicVolume", intMusicVolume);
                i.putExtra("intSoundVolume", intSoundVolume);
                startActivityForResult(i, 333);
            }
        });

        Player player = new Player();
        player.levelUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 999 && resultCode == RESULT_OK){
            // receive data
            System.out.println("Message = " + data.getIntExtra("message", 1));
        }
        else if (requestCode == 333 && resultCode == RESULT_OK){
            Bundle b = data.getExtras();
            intMusicVolume = (float) b.get("intMusicVolume");
            intSoundVolume = (float) b.get("intSoundVolume");
        }


        // 111 = LoadGame.  If Boolean confirmStart == true, time to load a new game.
        else if (requestCode == 111 && resultCode == RESULT_OK){
            System.out.println("Message = " + data.getIntExtra("message", 1));
            startGame();
        }

        // 222 = NewGame.  If Boolean confirmStart == true, time to create a new game.
        else if (requestCode == 222 && resultCode == RESULT_OK){
            System.out.println("Message = " + data.getIntExtra("message", 1));
            startGame();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startGame(){
        // Custom animation on image
        int runTime = 1000;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                /* Create an intent that will start the next activity. */
                Intent mainIntent = new Intent(MainActivity.this,
                        Game.class);
                mainIntent.putExtra("id", "1");

                //SplashScreen.this.startActivity(mainIntent);
                startActivity(mainIntent);

                /* Finish splash activity so user cant go back to it. */
                MainActivity.this.finish();

                /* Apply our splash exit (fade out) and main
                        entry (fade in) animation transitions. */
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
                Timer timer = new Timer(true);
                timer.schedule(new FadeMusicOut(), 0, 200);
            }
        }, runTime);

    }

}
