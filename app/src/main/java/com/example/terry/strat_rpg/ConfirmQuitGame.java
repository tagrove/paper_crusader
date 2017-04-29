package com.example.terry.strat_rpg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import static com.example.terry.strat_rpg.R.id.cancelQuitButton;


/**
 * This menu is for the user to confirm that they wish to quit the game.
 *
 */
public class ConfirmQuitGame extends Activity {

    private boolean confirmQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Gets the information regarding the current display
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // Gets rid of the white corners around the edges
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setLayout((int) (width*.5), (int) (height*.5));
        setContentView(R.layout.confirm_quit_window);

        //Initializes buttons to confirm or cancel starting a new game
        Button confirmButton = (Button) findViewById(R.id.confirmQuitButton);
        Button cancelButton = (Button) findViewById(cancelQuitButton);

        /*
         * Sets up the locations of the buttons.  This popup is currently a bit off,
         * so this code may need to be redone in the future.
         */
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmQuit = false;
                onBackPressed();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmQuit = true;
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        i.putExtra("confirmQuit", confirmQuit);
        finish();
        super.onBackPressed();
    }

}
