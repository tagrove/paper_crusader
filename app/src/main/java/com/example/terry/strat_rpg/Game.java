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
import android.support.v7.widget.AppCompatRatingBar;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
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
    private Monster monster;
    private Player player;
    private float musicVolume, soundVolume;
    private FrameLayout gameLayout;
    SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;

    private int saveSlot = 0;
    private File saveFile = null;

    //TODO - Organize monsters into some form of list so that they may be sequentially loaded as needed.
    ArrayList<Monster> monsterArrayList = new ArrayList<>();

    //TODO - Use to track how many monster of each type from the list have been killed.  When monsterCount = 5, next monster
    //TODO - will be a monster of the next type.  IE 1 1 1 1 1, 2 2 2 2 2, 3 3 3 3 3, BOSS
    private int goldEarnedThisRun = 0;
    private int monstersKilledThisRun = 0;
    private int experienceEarnedThisRun = 0;
    private boolean monsterAttacking = false;
    private boolean playerAttacking = false;
    private Random random = new Random();
    private TextToSpeech ttobj;
    private RatingBar playerHealthBar;


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

        // Retrieve Player information from MainActivity (which is read in from the save file)
        player = new Player();
        player = (Player) getIntent().getSerializableExtra("player");
        saveSlot = getIntent().getIntExtra("saveSlot", 0);

        // Used to make the demo a bit faster.  Normally the player will be inflated from the save file dat.
        try {
            saveFile = new File(getFilesDir() + "/Save_" + saveSlot);
            Scanner in = new Scanner(saveFile);
            String text = in.next();
            if (text == null)
                throw new InputMismatchException();

            if (text.equalsIgnoreCase("new")) {
                player.setAgentName("player_" + saveSlot);
                player.setCurrentHealth(50);
                player.setMaxHealth(50);
                player.setStrength(10);
            }
            else {
                player.setAgentName(text);
                player.setLevel(in.nextInt());
                player.setCurrentHealth(in.nextInt());
                player.setMaxHealth(in.nextInt());
                player.setStrength(in.nextInt());
                monstersKilledThisRun = in.nextInt();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            player.setAgentName("player_" + saveSlot);
            player.setCurrentHealth(50);
            player.setMaxHealth(50);
            player.setStrength(10);
        }

        monster = new Monster();

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
        playerHealthBar = (RatingBar) findViewById(R.id.playerHealthBar);
        playerHealthBar.setNumStars(1);
        updatePlayerHealthBar();

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

    /**
     * Contains the logic to see which agent will be swinging - either the player or the monster.
     * When the attacker is determined, the animation is called and the monster / player stats are
     * updated.
     */
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

        if (player.getTimeUntilAttack() < 0 || monster.getTimeUntilAttack() < 0){
            update();
        }
    }

    // TODO Calculate all necessary combat stats such as lifesteal, critical, block, etc.

    /**
     * updateStats will calculate whether or not a hit landed, if a critical hit landed,
     * or if the defender dodged.  Based on the results, the health of the defender (and possibly attacker)
     * will then be updated.
     *
     * @param attacker - whichever agent is attacking
     * @param defender - whichever agent is defending
     */
    public void updateStats(Agent attacker, Agent defender){

        int dodge = random.nextInt(100);
        int damage = attacker.getStrength();

        // The defender managed to dodge
        if (dodge < defender.getDodgeRate()){
            soundPool.play(4, soundVolume/10, soundVolume/10, 1, 0, 1f);
        }

        // The defender was hit
        else {
            int crit = random.nextInt(100);
            damage = damage - defender.getArmor();

            // Ensures that at least one point of damage is done (and does not allow for negative damage)
            if (damage <= 0){
                damage = 1;
            }

            // If a critical strike was made, the damage is doubled.  This formula may change in the future.
            if (crit < attacker.getCriticalRate()){
                soundPool.play(5, soundVolume/10, soundVolume/10, 1, 0, 1f);
                damage *= 2;
            }

            // A normal strike was made, no need for a multiplier.
            else {
                soundPool.play(2, soundVolume/10, soundVolume/10, 1, 0, 1f);
            }

            // Update defender's health based on damage received
            defender.setCurrentHealth(defender.getCurrentHealth() - damage);
            if (!defender.getAgentName().equalsIgnoreCase("monster") && !defender.getAgentName().equalsIgnoreCase("boss")){
               updatePlayerHealthBar();

            }

            // The defender died, take the appropriate action
            if (defender.getCurrentHealth() <= 0){
                if (defender.getAgentName().equalsIgnoreCase("monster") ||
                        defender.getAgentName().equalsIgnoreCase("boss")){
                    monster.setCurrentHealth(monster.getMaxHealth());
                    monsterDied();

                } else {
                    player.setCurrentHealth(player.getMaxHealth());
                    playerDied();

                }
            }
            // The defender did not die.  Any other miscellaneous actions may take place here if needed
            // else {}

        }
    }

    /**
     * Call when the player has defeated a monster.  In this method, we need to replace the monster portrait
     * and load the next monster object as the monster.
     */
    public void monsterDied(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        soundPool.play(7, soundVolume/10, soundVolume/10, 1, 0, 1f);
        // TODO Animate monster coming onto the screen
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), "Monster died, load next Monster",
                        Toast.LENGTH_SHORT).show();
            }
        });
        monstersKilledThisRun++;
        goldEarnedThisRun += monster.getGoldValue();
        experienceEarnedThisRun += monster.getExpValue();
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

        // Shoutout to Professor Fuchs :)
        if (monster.getAgentName().equalsIgnoreCase("boss")){
            ttobj.speak("We're all friends here", TextToSpeech.QUEUE_FLUSH, null);
        } else {
            soundPool.play(6, soundVolume/10, soundVolume/10, 1, 0, 1f);
        }

        Intent deathIntent = new Intent(Game.this, PlayerDiedPopUp.class);
        deathIntent.putExtra("expEarned", experienceEarnedThisRun);
        deathIntent.putExtra("goldEarned", goldEarnedThisRun);
        deathIntent.putExtra("monstersKilled", monstersKilledThisRun);
        deathIntent.putExtra("intSoundVolume", soundVolume);
        startActivity(deathIntent);


        // This is just for demo purposes.  Normally the monster queue resets, which requires
        // all of the 'current run' values to reset to 0.
        player.setCurrentHealth(player.getMaxHealth());
        updatePlayerHealthBar();

        goldEarnedThisRun = 0;
        experienceEarnedThisRun = 0;
        monstersKilledThisRun = 0;
        initializeMonster(monstersKilledThisRun);
        // TODO Open Activity that allows player to see stats of the current run

        // TODO Open Activity that allows player to purchase upgrades
    }

    @Override
    protected void onResume() {
        gameLayout.getForeground().setAlpha(0);
        super.onResume();
        this.resume();
    }

    public void resume() {
        running = true;
        if (!confirmQuit) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!confirmQuit) {
            gameLayout.getForeground().setAlpha(DIM_AMOUNT);
        }
        this.pause();
    }

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

        // The runOnUiThread is needed to update elements of the View since they are in separate threads.
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

                // TODO: Fix this so that the animations sync up properly
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

                // Enacts the appropriate animation on the correct image.
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

    /**
     * This is the actual loop that will then call methods to check for changes every so often.
     * Due to the nature of this program (user input is not responsive in terms of frames), the
     * check will happen once every 200 milliseconds, or 5 times per second.  There is no specific
     * need for this value, so it may be changed if desired.
     */
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

    /**
     * Gets the result from an activity that was finished.  The only two results currently
     * are from the options (to change sound / music volume) and from the home button
     * (to confirm to quit the game).
     *
     * This method can then dictate what exactly will happen next.
     * TODO: implement
     * @param requestCode - the request code initially sent out
     * @param resultCode - the result code received
     * @param data - the returning intent, including any extras
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle b = data.getExtras();
            // 888 = Go to MainActivity
            if (requestCode == 888 && resultCode == RESULT_OK) {
                // If confirmQuit = true, quit.
                confirmQuit = (boolean) b.get("confirmQuit");
                if (confirmQuit){
                    try {
                        PrintWriter out = new PrintWriter(saveFile);
                        out.println(player.getAgentName());
                        out.println(player.getLevel());
                        out.println(player.getCurrentHealth());
                        out.println(player.getMaxHealth());
                        out.println(player.getStrength());
                        out.println(monstersKilledThisRun);
                        out.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "EXCEPTION", Toast.LENGTH_SHORT).show();
                    }
                    quitGame();
                }
            } else if (requestCode == 333 && resultCode == RESULT_OK){
                soundVolume = (float) b.get("intSoundVolume");
                musicVolume = (float) b.get("intMusicVolume");
            }
        }
    }

    /**
     * Method called when the user wants to quit the game.  This method should either pass the
     * player object back to the MainActivity where it should be saved, or save the player
     * here.
     * TODO: Save the player object to file.
     */
    public void quitGame(){
        int runTime = 1000;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                /* Create an intent that will start the next activity. */
                Intent mainIntent = new Intent(Game.this, MainActivity.class);
                mainIntent.putExtra("intMusicVolume", musicVolume);
                mainIntent.putExtra("intSoundVolume", soundVolume);
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

    /**
     * Load the next monster in the line.  In the future, this could be randomized
     * or have a set pattern, but for now, we used a very simple logic of defeat X number
     * of monster1, Y number of monster2, Z number of monster3, then fight the boss.
     *
     * TODO: See about tying the image and sound effects used for each monster to the monster object
     * @param monsterNumber - how many monsters have been killed thus far
     */
    public void initializeMonster(int monsterNumber){
        if (monsterNumber < 1){
            // Slime
            monster = new Monster("monster", 15, 15, 1, 5, 5, 5, 5, 0, 0, 5, 5, 5, 1, 2);
            runOnUiThread(new Runnable() {
                public void run() {
                    monsterImage.setImageResource(R.drawable.slime);
                }
            });
        } else if (monsterNumber < 2){
            // Skeleton
            monster = new Monster("monster", 25, 25, 1, 8, 8, 4, 4, 0, 0, 5, 5, 5, 2, 5);
            runOnUiThread(new Runnable() {
                public void run() {
                    monsterImage.setImageResource(R.drawable.goblin);
                }
            });
        } else if (monsterNumber < 3){
            // Wizard
            monster = new Monster("monster", 30, 30, 1, 11, 11, 6, 6, 0, 0, 6, 6, 6, 5, 10);
            runOnUiThread(new Runnable() {
                public void run() {
                    monsterImage.setImageResource(R.drawable.skeleton_mage);
                }
            });

        } else {
            // Boss
            monster = new Monster("boss", 75, 75, 1, 20, 15, 6, 6, 0, 0, 5, 5, 5, 20, 50);
            runOnUiThread(new Runnable() {
                public void run() {
                    monsterImage.setImageResource(R.drawable.android_robot_evil);
                }
            });
        }
    }

    public void updatePlayerHealthBar(){


        playerHealthBar.setStepSize(player.getMaxHealth()/100);
        playerHealthBar.setMax(player.getMaxHealth());
        playerHealthBar.setRating(player.getCurrentHealth() * playerHealthBar.getStepSize());

        System.out.println("Current player health = " + player.getCurrentHealth());
        System.out.println("Max player health = " + player.getMaxHealth());
    }
}
