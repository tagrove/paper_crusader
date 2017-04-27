package com.example.terry.strat_rpg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.Iterator;
import java.util.Set;

import static android.R.attr.data;
import static android.media.ToneGenerator.MAX_VOLUME;
import static com.example.terry.strat_rpg.MainActivity.soundPool;

/**
 * CreateAndLoadGamePopUp will display a pop up dialog for Creating or Loading a new game.
 * Regardless of whether the player selects "New Game" or "Load Game", this class will be called.
 * The logic will simply change to see whether or not that current save slot is empty (if loading, create a new game),
 * or if the game is not empty (prompt if user wishes to delete that game in order to start a new game).
 * TODO - Add these logical checks for new / load game
 *
 */
public class CreateAndLoadGamePopUp extends Activity {

    MediaPlayer mpConfirm, mpCancel;
    public float intMusicVolume, intSoundVolume;
    private int saveSlot = -1;
    boolean confirmStart;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_save_pop_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        Intent i = getIntent();
        Bundle b = i.getExtras();

        intMusicVolume = (float)b.get("intMusicVolume");
        intSoundVolume = (float)b.get("intSoundVolume");

        // Gets rid of the white corners around the edges
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout((int) (width*.7), (int) (height*.7));

        RelativeLayout leftLayout = (RelativeLayout) findViewById(R.id.leftSaveSlot);
        RelativeLayout rightLayout = (RelativeLayout) findViewById(R.id.rightSaveSlot);
        RelativeLayout centerLayout = (RelativeLayout) findViewById(R.id.centerSaveSlot);

        centerLayout.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                System.out.println("Center Save File Clicked!");
                loadGame(v);
            }
        });

        rightLayout.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                System.out.println("Right Save File Clicked!");
                loadGame(v);
            }
        });


        leftLayout.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                System.out.println("Left Save File Clicked!");
                loadGame(v);
            }
        });

        TextView leftSaveText = (TextView) findViewById(R.id.leftSaveText);
        String testText = "random text can go here";
        leftSaveText.setText(testText);

        TextView centerSaveText = (TextView) findViewById(R.id.centerSaveText);
        centerSaveText.setText("01:17:04\nLevel: 06");

        TextView rightSaveText = (TextView) findViewById(R.id.rightSaveText);
        testText = "New Game";
        rightSaveText.setText(testText);

        ImageView rightSaveImage = (ImageView) findViewById(R.id.rightSaveImage);
        rightSaveImage.setImageDrawable(null);

        int saveFilePosition = 0;
        Intent intent = new Intent();
        intent.putExtra("saveFilePosition",saveFilePosition); // data is the value you need in parent
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

    /**
     * Passes the save slot to use the confirmation window
     * TODO - Add messages based on whether or not a new game is being overwritten, or if loading a blank game
     * @param v current view
     */
    public void loadGame(View v){
        saveSlot = -1;
        if (v == findViewById(R.id.leftSaveSlot)){
            saveSlot = 0;
        } else if (v == findViewById(R.id.centerSaveSlot)){
            saveSlot = 1;
        } else if (v == findViewById(R.id.rightSaveSlot)) {
            saveSlot = 2;
        }

        // Load data, then send it to ConfirmationPopUp
        soundPool.play(1, intSoundVolume / 10, intSoundVolume / 10, 1, 0, 1f);
        Intent i = new Intent(CreateAndLoadGamePopUp.this, ConfirmationPopUp.class);
        i.putExtra("saveSlot", saveSlot);
        i.putExtra("intMusicVolume", intMusicVolume);
        i.putExtra("intSoundVolume", intSoundVolume);

        // Loading the game
        startActivityForResult(i, 222);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 222 && resultCode == RESULT_OK){
            // receive data
            Bundle b = data.getExtras();
            confirmStart = (boolean) b.get("confirmStart");
            saveSlot = (int) b.get("saveSlot");
            data.putExtra("saveSlot", saveSlot);

            // If confirmStart == true, the game is ready to load the save file
            if (confirmStart){
                onBackPressed();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
