package com.example.terry.strat_rpg;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class Game extends AppCompatActivity implements View.OnClickListener{

    public RelativeLayout equipmentLayout, talentsLayout, settingsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        final ImageView backgroundOne = (ImageView) findViewById(R.id.background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.background_two);
        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, -1.0f);

        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.setDuration(120000L);
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
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

        final ImageView playerImage = (ImageView) findViewById(R.id.player_image);
        final ImageView monsterImage = (ImageView) findViewById(R.id.monster_image);
        final float monsterLeft = monsterImage.getX();
        System.out.println(monsterLeft + " = MonsterLeft");
        final Animation alpha = AnimationUtils.loadAnimation( getApplication(), R.anim.anim_alpha);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.armor_icon_layout: {
                System.out.println("Do something with armor icon!");

                /**
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ImageView playerImage = (ImageView) findViewById(R.id.player_image);
                        Animation alpha = AnimationUtils.loadAnimation( getApplication(), R.anim.anim_alpha);
                        playerImage.startAnimation(alpha);
                    }
                }, 1000);
                */
                ImageView playerImage = (ImageView) findViewById(R.id.player_image);
                TranslateAnimation animation = new TranslateAnimation(0.0f, 1200,
                        0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
                animation.setDuration(500);  // animation duration
                animation.setRepeatCount(1);  // animation repeat count
                animation.setRepeatMode(2);   // repeat animation (left to right, right to left )

                playerImage.startAnimation(animation);  // start animation

                break;
            }case R.id.talents_icon_layout: {
                System.out.println("Do something with Talents Icon!");

                final ImageView playerImage = (ImageView) findViewById(R.id.player_image);
                final TranslateAnimation animation = new TranslateAnimation(0.0f, 1200,
                        0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
                animation.setDuration(500);  // animation duration
                animation.setRepeatCount(1);  // animation repeat count
                animation.setRepeatMode(2);   // repeat animation (left to right, right to left )
                playerImage.startAnimation(animation);  // start animation

                final ImageView monsterImage = (ImageView) findViewById(R.id.monster_image);


                /////////////////////

                final Animation animation1 = new AlphaAnimation(0.0f, 1.0f);
                animation1.setDuration(1000);
                animation1.setStartOffset(5000);

                //animation1 AnimationListener
                animation1.setAnimationListener(new Animation.AnimationListener(){

                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        // start animation1 when animation2 ends (repeat)
                        monsterImage.startAnimation(animation1);
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

                monsterImage.startAnimation(animation1);

                final Animation animation2 = new AlphaAnimation(1.0f, 0.0f);
                animation2.setDuration(1000);
                animation2.setStartOffset(5000);

                //animation2 AnimationListener
                animation2.setAnimationListener(new Animation.AnimationListener(){
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        // start animation2 when animation1 ends (continue)
                        monsterImage.startAnimation(animation2);
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



                //////////////






                break;
            }case R.id.settings_icon_layout: {
                System.out.println("Do something with Settings Icon!");

                break;
            }
        }


    }


}
