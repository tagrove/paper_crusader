package com.example.terry.strat_rpg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

/**
 * This class exists merely as a template for new dialogs.
 *
 */
public class PlayerDiedPopUp extends Activity {

    MediaPlayer mpConfirm;
    private String monstersKilledThisRun, expEarnedThisRun, goldEarnedThisRun;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Gets the information regarding the current display
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Gets rid of the white corners around the edges
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout((int) (width*.95), (int) (height*.95));
        setContentView(R.layout.player_died_pop_window);

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


        Intent i = getIntent();
        Bundle b = i.getExtras();

        monstersKilledThisRun = Integer.toString(b.getInt("monstersKilled"));
        goldEarnedThisRun = Integer.toString(b.getInt("goldEarned"));
        expEarnedThisRun = Integer.toString(b.getInt("expEarned"));

        ImageView playerImage = (ImageView) findViewById(R.id.playerDeathImage);
        playerImage.setImageResource(R.drawable.crusader_placeholder_alpha);

        TextView monstersKilledTextView = (TextView) findViewById(R.id.monstersDefeatedText);
        TextView goldTextView = (TextView) findViewById(R.id.goldEarnedText);
        TextView expTextView = (TextView) findViewById(R.id.expEarnedText);

        goldTextView.setText(goldEarnedThisRun);
        expTextView.setText(expEarnedThisRun);
        monstersKilledTextView.setText(monstersKilledThisRun);

        //Initializes sound with the correct sound volume from MainActivity

        mpConfirm = MediaPlayer.create(super.getApplicationContext(), R.raw.crusader_menu_confirm);
        Button confirmButton = (Button) findViewById(R.id.playerDiedConfirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpConfirm = MediaPlayer.create(PlayerDiedPopUp.this, R.raw.crusader_menu_confirm);
                mpConfirm.start();
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }




}
