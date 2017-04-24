package com.example.terry.strat_rpg;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;


/**
 * AnimationThread - this class is a helper to instantiate an animation thread
 * that repaints a view a regular intervals
 *
 * Created by clintf on 3/29/17.
 * @author Clint Fuchs
 * @date 3/29/2017
 * @email clintf@coastal.edu
 * @course CSCI 343
 */

public class AnimationThread {

    public static final float DEFAULT_FPS = 30;

    /**
     * View that references the view to be redrawn
     */
    private View view;
    private Thread thread;
    private float framesPerSecond;
    private Handler handler;
    private volatile boolean isRunning;

    /**
     * Constructor that initializes a this
     *
     * @param aView to be redrawn and not null
     * @param fps should be > 0
     */
    public AnimationThread(View aView, float fps){
        view = aView;
        framesPerSecond = fps;
        isRunning = false;
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * indicates whether this thread is running
     * @return true if the thread is running and false otherwise
     */
    public boolean isRunning(){
        return isRunning;
    }

    /**
     * starts the drawing thread
     */
    public void start(){
        if(thread == null){
            thread = new Thread(new ThreadRunner());
            thread.start();
        }
    }

    /**
     * stops the drawing thread
     */
    public void stop(){
        if(thread != null){
            try {
                thread.join();
            }catch(InterruptedException ie){
                Log.e("AnimationThread", "Thread join interrupted...");
            }
            thread = null;
            isRunning = false;
        }
    }

    /**
     * private inner class thread used by the drawing thread
     *     for animation purposes.
     */
    private class ThreadRunner implements  Runnable {
        @Override
        public void run(){
            isRunning = true;
            while(isRunning){
                try {
                    Thread.sleep((long) (1000 / framesPerSecond));

                    handler.post(new Updater());
                }catch(InterruptedException ie){
                    Log.e("ThreadRunner", "Thread sleep interrupted....");
                }
            }
        }
    }

    /**
     * private inner class thread used by the drawing thread to invalidate
     *     the view to be redrawn
     */
    private class Updater implements Runnable {
        @Override
        public void run(){
            view.invalidate();
        }
    }
}

