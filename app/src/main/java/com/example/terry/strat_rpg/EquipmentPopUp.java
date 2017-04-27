package com.example.terry.strat_rpg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.RatingBar;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import static com.example.terry.strat_rpg.MainActivity.soundPool;
import static com.example.terry.strat_rpg.R.id.musicDecreaseVolume;

/**
 * EquipmentPopUp - Allows the player to view purchased equipment, or to buy new equipment
 */
public class EquipmentPopUp extends Activity implements View.OnClickListener {

    public float intSoundVolume;
    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.equipment_pop_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;


        TabHost host = (TabHost) findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Tab One");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Tab Two");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Tab Three");
        host.addTab(spec);


        // Prevents the popup from being closeable when the user clicks outside of the dialog
        setFinishOnTouchOutside(false);

        // Gets rid of the white corners around the edges
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Scales size of window to be smaller
        getWindow().setLayout((int) (width*.8), (int) (height*.8));

        Intent i = getIntent();
        Bundle b = i.getExtras();
        intSoundVolume = (float)b.get("intSoundVolume");

    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {

    }


}
