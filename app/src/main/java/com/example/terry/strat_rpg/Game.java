package com.example.terry.strat_rpg;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;

/**
 * Game class for Paper Crusader
 * This class is the activity that will actually run the game loop.  The toolbar will allow
 * the player to navigate through the various upgrades they may purchase.
 *
 * TODO - Add the health bar to the top of the screen
 * TODO - Add a window to display current level and currency available
 * TODO - Add animation for monster death, including EXP and Money granted
 * TODO - Add dialogs for the Equipment and Talent sections
 * TODO - Add the functionality behind the Options button so that it may take the player back to the main activity (Quit game)
 * TODO - Add functionality to Options to where the sound / music levels will save and return to main
 */
public class Game extends AppCompatActivity implements View.OnClickListener, Runnable {


    private boolean running = true;
    private static final int DIM_AMOUNT = 220;
    private boolean confirmQuit = false;
    private int timeBetweenTicks = 100;
    private Thread gameThread = null;
    private ImageView playerImage, monsterImage;
    private Agent player, monster;
    private float musicVolume, soundVolume;
    private FrameLayout gameLayout;
    SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;

    //TODO - Organize monsters into some form of list so that they may be sequentially loaded as needed.
    ArrayList<Monster> monsterArrayList = new ArrayList<Monster>();

    //TODO - Use to track how many monster of each type from the list have been killed.  When monsterCount = 5, next monster
    //TODO - will be a monster of the next type.  IE 1 1 1 1 1, 2 2 2 2 2, 3 3 3 3 3, BOSS
    private int goldEarnedThisRun = 0;
    private int monstersKilledThisRun = 0;
    private int experienceEarnedThisRun = 0;
    private boolean monsterAttacking = false;
    private boolean playerAttacking = false;
    private Random random = new Random();
    private TextToSpeech ttobj;


    public RelativeLayout equipmentLayout, talentsLayout, settingsLayout, homeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        musicVolume = (float) bundle.get("intMusicVolume");
        soundVolume = (float) bundle.get("intSoundVolume");

        setContentView(R.layout.activity_game);
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


        // Set up the background so that it has two images in order to have the
        // infinite scrolling background.
        final ImageView backgroundOne = (ImageView) findViewById(R.id.background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two);
        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, -1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(120000L);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX + width);
            }
        });

        equipmentLayout = (RelativeLayout) findViewById(R.id.armor_icon_layout);
        equipmentLayout.setOnClickListener(this);
        talentsLayout = (RelativeLayout) findViewById(R.id.talents_icon_layout);
        talentsLayout.setOnClickListener(this);
        settingsLayout = (RelativeLayout) findViewById(R.id.settings_icon_layout);
        settingsLayout.setOnClickListener(this);
        homeLayout = (RelativeLayout) findViewById(R.id.home_icon_layout);
        homeLayout.setOnClickListener(this);


        // TODO - Figure out what animations are going to be used and create these ImageViews in the correct place.
        monsterImage = (ImageView) findViewById(R.id.monster_image);
        playerImage = (ImageView) findViewById(R.id.player_image);

        // Retrieve Player information from MainActivity (which read from save file)
        player = new Player();
        player = (Player) getIntent().getSerializableExtra("player");
        // Temporarily needs to be here for the combat logic to work
        player.setAgentName("player");
        player.setCurrentHealth(50);
        player.setMaxHealth(50);

        monster = new Monster();
        // Temporarily needs to be here for the combat logic to work
        monster.setAgentName("monster");
        monster.setAttackSpeed(5.0f);


        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<>();

        // Load sounds for later use
        soundPoolMap.put(1, soundPool.load(this, R.raw.melee_2_sound, 1));
        soundPoolMap.put(2, soundPool.load(this, R.raw.slime_attack_sound, 1));
        soundPoolMap.put(3, soundPool.load(this, R.raw.melee_3_sound, 1));
        soundPoolMap.put(4, soundPool.load(this, R.raw.dodge_sound, 1));
        soundPoolMap.put(5, soundPool.load(this, R.raw.critical_sound, 1));
        soundPoolMap.put(6, soundPool.load(this, R.raw.player_died, 1));
        soundPoolMap.put(7, soundPool.load(this, R.raw.monster_died, 1));

        playerImage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                soundPool.play(2, soundVolume, soundVolume, 1, 0, 1f);
            }

        });

        gameLayout = (FrameLayout) findViewById( R.id.activity_game);
        gameLayout.getForeground().setAlpha( 0);

        initializeMonster(monstersKilledThisRun);

        ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                ttobj.setLanguage(Locale.UK);
                ttobj.setPitch(.5f);

            }
        });

    }

    /**
     * onClick here is going to handle all of the toolbar clicks and their corresponding actions.
     * TODO - Work on this entire section.  Figure out what animations to do and implement them correctly.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.armor_icon_layout: {
                final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
                view.startAnimation(animAlpha);
                Intent i = new Intent(Game.this, EquipmentPopUp.class);
                i.putExtra("intSoundVolume", soundVolume);

                // Code 555 = Purchase Gear
                startActivityForResult(i, 555);

                break;
            }case R.id.talents_icon_layout: {
                System.out.println("Do something with Talents Icon!");

                break;
            }case R.id.settings_icon_layout: {
                final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
                    view.startAnimation(animAlpha);
                    Intent i = new Intent(Game.this, OptionsPopUp.class);
                    i.putExtra("intMusicVolume", musicVolume);
                    i.putExtra("intSoundVolume", soundVolume);

                    // Code 333 = Options
                    startActivityForResult(i, 333);
                break;
            }case R.id.home_icon_layout: {
                final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
                view.startAnimation(animAlpha);
                Intent i = new Intent(Game.this, ConfirmQuitGame.class);
                i.putExtra("confirmQuit", confirmQuit);
                startActivityForResult(i, 888);
                break;
            }
        }
    }

    public void update() {

        // Delay is designed to allow the animation to have time to complete while the player or
        // monster attacks.  An alternate route to try may be to put the thread to sleep so that
        // the player / monster is not unfairly 'stunned' for a period of time if both are ready
        // to attack at the same time.
        int delay = 800;
        player.setTimeUntilAttack(player.getTimeUntilAttack() - timeBetweenTicks);
        monster.setTimeUntilAttack(monster.getTimeUntilAttack() - timeBetweenTicks);

        // Check if player is ready to attack
        if (player.getTimeUntilAttack() < 0 ){

            // If true, both are ready, but monster was ready first
            if (monster.getTimeUntilAttack() < player.getTimeUntilAttack()){
                // Monster Attack
                animateAttack(monster.getAgentName());
                monster.setTimeUntilAttack(monster.getAttackSpeed() * 1000);
                monsterAttacking = true;
                // Delay Player so that the animation may finish
                player.setTimeUntilAttack(player.getTimeUntilAttack() + delay);
            }

            // If false, this means that the player must have priority
            // and needs to attack
            else {
                player.setTimeUntilAttack(player.getAttackSpeed() * 1000);
                animateAttack(player.getAgentName());
                playerAttacking = true;
                // Monster also was ready to attack, but player was ready first
                if (monster.getTimeUntilAttack() < 0){
                    monster.setTimeUntilAttack(monster.getTimeUntilAttack() + delay);
                }
            }
        } else if (monster.getTimeUntilAttack() < 0){
            // Monster Attacks only
            animateAttack(monster.getAgentName());
            monster.setTimeUntilAttack(monster.getAttackSpeed() * 1000);
            monsterAttacking = true;
        }

        if (monsterAttacking){
            soundPool.play(2, soundVolume/10, soundVolume/10, 1, 0, 1f);
            updateStats(monster, player);

        } else if (playerAttacking){
            updateStats(player, monster);
        }

        monsterAttacking = false;
        playerAttacking = false;
    }

    // TODO Calculate all necessary combat stats such as lifesteal, critical, block, etc.

    public void updateStats(Agent attacker, Agent defender){

        int dodge = random.nextInt(100);
        int damage = attacker.getStrength();

        if (dodge < defender.getDodgeRate()){
            soundPool.play(4, soundVolume/10, soundVolume/10, 1, 0, 1f);
        } else {
            int crit = random.nextInt(100);
            damage = damage - defender.getArmor();
            if (damage <= 0){
                damage = 1;
            }
            if (crit < attacker.getCriticalRate()){
                soundPool.play(5, soundVolume/10, soundVolume/10, 1, 0, 1f);
                damage *= 2;
            } else {
                soundPool.play(2, soundVolume/10, soundVolume/10, 1, 0, 1f);
            }

            defender.setCurrentHealth(defender.getCurrentHealth() - damage);
            if (defender.getCurrentHealth() <= 0){
                if (defender.getAgentName().equalsIgnoreCase("player")){
                    player.setCurrentHealth(player.getMaxHealth());
                    playerDied();
                } else {
                    monster.setCurrentHealth(monster.getMaxHealth());
                    monsterDied();
                }
            } else {
                // Update any other stats needing to be updated - this is where combat damage was dealt,
                // but nobody has died.
            }
        }
    }

    /**
     * Call when the player has defeated a monster.  In this method, we need to replace the monster portrait
     * and load the next monster object as the monster.
     */
    public void monsterDied(){
        System.out.println("PLAYER DEFEATED THE MONSTER!");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        soundPool.play(7, soundVolume/10, soundVolume/10, 1, 0, 1f);
        // TODO Animate monster coming onto the screen

        monstersKilledThisRun++;
        System.out.println("Number of monsters killed: " + monstersKilledThisRun);
        initializeMonster(monstersKilledThisRun);

        // TODO Award Experience / Gold
    }

    /**
     * This is called when the player receives lethal damage.
     * At this point, either the activity could be finished, or a popup could be called and go away while resetting all
     * of the data on this current activity.
     */
    public void playerDied(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (monster.getAgentName().equalsIgnoreCase("boss")){
            ttobj.speak("We're all friends here", TextToSpeech.QUEUE_FLUSH, null);
        } else {
            soundPool.play(6, soundVolume/10, soundVolume/10, 1, 0, 1f);
        }

        player.setCurrentHealth(player.getMaxHealth());
        //goldEarnedThisRun = 0;
        //experienceEarnedThisRun = 0;
        //monstersKilledThisRun = 0;
        // TODO Open Activity that allows player to see stats of the current run

        // TODO Open Activity that allows player to purchase upgrades


    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {

        gameLayout.getForeground().setAlpha(0);

        super.onResume();
        this.resume();
    }

    // If SimpleGameEngine Activity is started then
    // start our thread.
    public void resume() {
        running = true;
        if (!confirmQuit) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();
        if (!confirmQuit) {
            gameLayout.getForeground().setAlpha(DIM_AMOUNT);
        }
        this.pause();
    }

    // If SimpleGameEngine Activity is paused/stopped
    // shutdown our thread.
    public void pause() {
        running = false;

        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    /**
     * Displays the animation of the attack for the player or the monster.
     * @param name - determines who the attacker is to apply the appropriate animation
     */
    public void animateAttack(String name){

        final String attackerName = name;
        runOnUiThread(new Runnable() {
            public void run() {

                TranslateAnimation animation;
                if (attackerName.equalsIgnoreCase("monster") || attackerName.equalsIgnoreCase(("boss"))){
                    animation = new TranslateAnimation(0.0f, -1200, 0.0f, 0.0f);
                } else {
                    animation = new TranslateAnimation(0.0f, 1200, 0.0f, 0.0f);
                }

                animation.setDuration(200);  // animation duration
                animation.setRepeatCount(1);  // animation repeat count
                animation.setRepeatMode(2);   // repeat animation (left to right, right to left )

                // TODO: Fix this so that it syncs up properly
                final Animation animation2 = new AlphaAnimation(1.0f, 0.0f);
                animation2.setDuration(3);
                animation2.setRepeatCount(3);
                monsterImage.startAnimation(animation2);
                animation2.setAnimationListener(new Animation.AnimationListener(){
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        // start animation2 when animation1 ends (continue)
                        //monsterImage.startAnimation(animation2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onAnimationStart(Animation arg0) {
                        // TODO Auto-generated method stub
                    }
                });

                if (attackerName.equalsIgnoreCase("monster") || attackerName.equalsIgnoreCase(("boss"))){
                    monsterImage.startAnimation(animation);
                    playerImage.startAnimation(animation2);

                } else {
                    monsterImage.startAnimation(animation2);
                    playerImage.startAnimation(animation);
                }
            }
        });
    }

    public void run() {
        long lastTime = System.nanoTime();
        double ns = 1000000000;
        double delta = 0;
        long timer = System.currentTimeMillis();

        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1){
                delta--;
            }

            // Originally 1000, which checks every second.
            if(System.currentTimeMillis() - timer > timeBetweenTicks){
                timer += timeBetweenTicks;
                update();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            Bundle b = data.getExtras();

            // 888 = go home
            if (requestCode == 888 && resultCode == RESULT_OK) {
                // If confirmQuit = true, quit.
                confirmQuit = (boolean) b.get("confirmQuit");
                if (confirmQuit){
                    quitGame();
                }
            } else if (requestCode == 333 && resultCode == RESULT_OK){
                soundVolume = (float) b.get("intSoundVolume");
                musicVolume = (float) b.get("intMusicVolume");
            }

        }
    }


    public void quitGame(){
        int runTime = 1000;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                /* Create an intent that will start the next activity. */
                Intent mainIntent = new Intent(Game.this, MainActivity.class);
                mainIntent.putExtra("intMusicVolume", musicVolume);
                mainIntent.putExtra("intSoundVolume", soundVolume);

                System.out.println("About to send Sound back to main, sound = " + soundVolume);
                startActivity(mainIntent);

                /* Finish activity so user cannot go back to it.
                *  If user needs to return to the main activity, a new activity will be created*/
                Game.this.finish();

                /* Applies slide out / slide in animations */
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
                Timer timer = new Timer(true);

                // TODO - figure out how to make the music actually fade out.
                timer.schedule(new FadeMusicOut(), 0, 200);
            }
        }, runTime);
    }

    public void initializeMonster(int monsterNumber){
        if (monsterNumber < 1){
            // Slime
            monster = new Monster("monster", 15, 15, 1, 5, 5, 5, 5, 0, 0, 5, 5, 5);
            runOnUiThread(new Runnable() {
              public void run() {
                  monsterImage.setImageResource(R.drawable.slime);
              }
            });

        } else if (monsterNumber < 2){
            // Skeleton
            monster = new Monster("monster", 25, 25, 1, 8, 8, 4, 4, 0, 0, 5, 5, 5);
            runOnUiThread(new Runnable() {
                public void run() {
                    monsterImage.setImageResource(R.drawable.goblin);
                }
            });


        } else if (monsterNumber < 3){
            // Wizard
            monster = new Monster("monster", 30, 30, 1, 11, 11, 6, 6, 0, 0, 6, 6, 6);
            runOnUiThread(new Runnable() {
                public void run() {
                    monsterImage.setImageResource(R.drawable.skeleton_mage);
                }
            });

        } else {
            // Boss
            monster = new Monster("boss", 75, 75, 1, 20, 15, 6, 6, 0, 0, 3, 3, 3);
            runOnUiThread(new Runnable() {
                public void run() {
                    monsterImage.setImageResource(R.drawable.android_robot_evil);
                }
            });

        }

    }
}
