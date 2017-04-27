package com.example.terry.strat_rpg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import static android.media.ToneGenerator.MAX_VOLUME;


/**
 * This class exists merely as a template for new dialogs.
 *
 */
public class PopUpDialogExample extends Activity {

    MediaPlayer mpCancel, mpConfirm;
    boolean confirmStart;

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
        getWindow().setLayout((int) (width*.5), (int) (height*.5));
        setContentView(R.layout.popwindow);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        @SuppressWarnings("unused") int saveSlot = (int)b.get("saveSlot");

        //Initializes sound with the correct sound volume from MainActivity
        float intSoundVolume = (float)b.get("intSoundVolume");
        mpCancel = MediaPlayer.create(super.getApplicationContext(), R.raw.crusader_menu_cancel);
        float soundVolume = (float)(Math.log(MAX_VOLUME - intSoundVolume)/Math.log(MAX_VOLUME));
        mpCancel.setVolume(1-soundVolume, 1-soundVolume);
        mpCancel.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.reset();
                mp.release();
                //noinspection UnusedAssignment
                mp = null;
            }

        });

        mpConfirm = MediaPlayer.create(super.getApplicationContext(), R.raw.crusader_menu_confirm);
        mpCancel.setVolume(1-soundVolume, 1-soundVolume);
        mpCancel.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.reset();
                mp.release();
                //noinspection UnusedAssignment
                mp = null;
            }

        });


        //Initializes buttons to confirm or cancel starting a new game
        Button confirmButton = (Button) findViewById(R.id.confirmButton);
        Button cancelButton = (Button) findViewById(R.id.cancelQuitButton);


        /*
         * Sets up the locations of the buttons.  This popup is currently a bit off,
         * so this code may need to be redone in the future.
         */
        final ViewGroup.LayoutParams confirmParams = confirmButton.getLayoutParams();
        final ViewGroup.LayoutParams cancelParams = cancelButton.getLayoutParams();
        cancelButton.setLayoutParams(cancelParams);
        confirmButton.setLayoutParams(confirmParams);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpCancel = MediaPlayer.create(PopUpDialogExample.this, R.raw.crusader_menu_cancel);
                mpCancel.start();
                confirmStart = false;
                onBackPressed();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Time to start a save game!");
                mpConfirm = MediaPlayer.create(PopUpDialogExample.this, R.raw.crusader_begin_new_save);
                mpConfirm.start();
                confirmStart = true;
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent();
        i.putExtra("message", 4);
        i.putExtra("confirmStart", confirmStart);
        setResult(RESULT_OK, i);
        finish();
        super.onBackPressed();

    }




}
