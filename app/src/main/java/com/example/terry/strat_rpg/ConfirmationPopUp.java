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
import android.widget.TextView;

import static android.media.ToneGenerator.MAX_VOLUME;

/**
 * Displays a dialog to the user confirming their selection for creating or loading a game.
 * The message will be tailored to the particular save slot.
 *
 * The dialog should also reflect whether or not the user has chosen an "inappropriate" action.
 * An inappropriate action in this case would be creating a file where a file already exists,
 * or loading a file where no file exists.  If either of these actions should occur, the dialog
 * will inform the user so that the user must confirm that they will be starting a new game and
 * possibly deleting an old game.
 * TODO - Add the functionality of the above
 */
public class ConfirmationPopUp extends Activity {

    private MediaPlayer mpCancel, mpConfirm;
    private boolean confirmStart;
    private int saveSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Gets the information regarding the current display
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Prevents the popup from being closeable when the user clicks outside of the dialog
        setFinishOnTouchOutside(false);

        // Gets rid of the white corners around the edges
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout((int) (width*.5), (int) (height*.5));
        setContentView(R.layout.popwindow);

        Intent i = getIntent();
        Bundle b = i.getExtras();

        saveSlot = (int)b.get("saveSlot");
        TextView confirmText = (TextView) findViewById(R.id.loadGameText);
        String gameText = "Load Game " + saveSlot + "?";
        confirmText.setText(gameText);


        // Initializes sound with the correct volume settings from MainActivity
        float intSoundVolume = (float)b.get("intSoundVolume");
        mpCancel = MediaPlayer.create(super.getApplicationContext(), R.raw.crusader_menu_cancel);
        float soundVolume = (float)(Math.log(MAX_VOLUME - intSoundVolume)/Math.log(MAX_VOLUME));
        mpCancel.setVolume(1-soundVolume, 1-soundVolume);

        // TODO - Find the best way to implement these sounds.  Is setting mp = null necessary?
        mpCancel.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @SuppressWarnings("UnusedAssignment")
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp = null;
            }

        });

        mpConfirm = MediaPlayer.create(super.getApplicationContext(), R.raw.crusader_menu_confirm);
        mpCancel.setVolume(1-soundVolume, 1-soundVolume);
        mpCancel.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @SuppressWarnings("UnusedAssignment")
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp = null;
            }
        });

        // Initializes buttons to confirm or cancel
        Button confirmButton = (Button) findViewById(R.id.confirmButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);

        // Programmatically assigns locations of the buttons.
        // This code currently has some major issues, but looks okay on a specific device.
        // TODO - Rework this entire section when we get time.
        final ViewGroup.LayoutParams confirmParams = confirmButton.getLayoutParams();
        final ViewGroup.LayoutParams cancelParams = cancelButton.getLayoutParams();
        cancelButton.setLayoutParams(cancelParams);
        confirmButton.setLayoutParams(confirmParams);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpCancel = MediaPlayer.create(ConfirmationPopUp.this, R.raw.crusader_menu_cancel);
                mpCancel.start();
                confirmStart = false;
                onBackPressed();
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Time to start a save game!");
                mpConfirm = MediaPlayer.create(ConfirmationPopUp.this, R.raw.crusader_begin_new_save);
                mpConfirm.start();
                confirmStart = true;
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("confirmStart", confirmStart);
        i.putExtra("saveSlot", saveSlot);
        setResult(RESULT_OK, i);
        finish();
        super.onBackPressed();
    }
}
