package com.example.terry.strat_rpg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;

import static android.media.ToneGenerator.MAX_VOLUME;
import static com.example.terry.strat_rpg.MainActivity.mpOpening;

public class LoadSavePopUp extends Activity {

    MediaPlayer mpConfirm, mpCancel;
    public float intMusicVolume, intSoundVolume;
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

        mpCancel = MediaPlayer.create(super.getApplicationContext(), R.raw.crusader_menu_cancel);
        float soundVolume = (float)(Math.log(MAX_VOLUME - intSoundVolume)/Math.log(MAX_VOLUME));
        mpCancel.setVolume(1-soundVolume, 1-soundVolume);


        mpConfirm = MediaPlayer.create(super.getApplicationContext(), R.raw.crusader_menu_confirm);
        mpConfirm.setVolume(1-soundVolume, 1-soundVolume);


        // Gets rid of the white corners around the edges
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout((int) (width*.7), (int) (height*.7));

        LinearLayout layout = (LinearLayout) findViewById(R.id.loadLinearLayout);
        RelativeLayout leftLayout = (RelativeLayout) findViewById(R.id.leftSaveSlot);
        RelativeLayout rightLayout = (RelativeLayout) findViewById(R.id.rightSaveSlot);
        RelativeLayout centerLayout = (RelativeLayout) findViewById(R.id.centerSaveSlot);

        centerLayout.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                System.out.println("Center Save File Clicked!");
                // Load Game
                loadGame(v);
            }
        });

        rightLayout.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                System.out.println("Right Save File Clicked!");
                // Load Game
                loadGame(v);
            }
        });


        leftLayout.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View v){
                System.out.println("Left Save File Clicked!");
                // Load Game
                loadGame(v);
            }
        });

        TextView leftSaveText = (TextView) findViewById(R.id.leftSaveText);
        leftSaveText.setText("stupid text goes here");

        TextView centerSaveText = (TextView) findViewById(R.id.centerSaveText);
        centerSaveText.setText("01:17:04\nLevel: 06");

        TextView rightSaveText = (TextView) findViewById(R.id.rightSaveText);
        rightSaveText.setText("New Game");

        ImageView rightSaveImage = (ImageView) findViewById(R.id.rightSaveImage);
        rightSaveImage.setImageDrawable(null);

        int saveFilePosition = 0;
        Intent intent = new Intent();
        intent.putExtra("saveFilePosition",saveFilePosition); // data is the value you need in parent
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

    public void loadGame(View v){
        int saveSlot = -1;
        if (v == findViewById(R.id.leftSaveSlot)){
            System.out.println("Confirmed in method, left was clicked");
            saveSlot = 0;

        } else if (v == findViewById(R.id.centerSaveSlot)){
            System.out.println("Function called to load center save");
            saveSlot = 1;

        } else if (v == findViewById(R.id.rightSaveSlot)) {
            System.out.println("Function called to load right save");
            saveSlot = 2;
        }

        // Load data, then send it to Pop
        mpConfirm.start();
        Intent i = new Intent(LoadSavePopUp.this, Pop.class);
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
            System.out.println("Message = " + data.getIntExtra("message", 1));
            Bundle b = data.getExtras();
            confirmStart = (boolean) b.get("confirmStart");

            // If confirmStart == true, the game is ready to load the save file
            if (confirmStart){
                onBackPressed();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
