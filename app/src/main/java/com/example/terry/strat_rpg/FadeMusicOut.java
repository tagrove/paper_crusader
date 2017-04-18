package com.example.terry.strat_rpg;

import java.util.TimerTask;

import static com.example.terry.strat_rpg.MainActivity.mpOpening;

/**
 * Created by Terry on 4/14/2017.
 */

public class FadeMusicOut extends TimerTask {
    public void run(){

        MainActivity.mpOpening.setVolume(0,0);

    }

}
