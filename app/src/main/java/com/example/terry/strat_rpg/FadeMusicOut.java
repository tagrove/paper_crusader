package com.example.terry.strat_rpg;

import java.util.TimerTask;

import static com.example.terry.strat_rpg.MainActivity.mpOpening;

/**
 * FadeMusicOut is a class that will allow the music to fade over time.
 * This is currently not working as intended and will need to be fixed in the future
 * TODO - Add the functionality to decrease the music volume by a particular step size over time
 *
 * This class is called by MainActivity
 */
public class FadeMusicOut extends TimerTask {
    public void run(){
        MainActivity.mpOpening.setVolume(0,0);
    }
}
