package com.example.android.jailpoker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static java.lang.System.currentTimeMillis;

public class Play extends AppCompatActivity {
    int nRounds = 0, ones, twos;
    private int scoreHuman = 0, scoreCPU = 0;
    boolean isPlaying = false, roundEnded = false;
    long roundEndedTime;
    final int maxDelay = 300;
    final int countDownTime = 500;
    Handler round;
    Animation fade;
    TextView textView;
    ImageView CPUImageview;
    ImageView humanImageview;
    int countDownDigit;
    boolean clicked;
    // moves
    int CPU, human;
    private final Random random = new Random();
    // these two Runnables are used to handle events that occur during each round
    private final Runnable countDown = new Runnable() {
        @Override
        public void run() {
            textView.setText(countDownDigit + "");
            textView.startAnimation(fade);
            --countDownDigit;
        }
    };
    private final Runnable onRoundEnd = new Runnable() {
        @Override
        public void run() {
            textView.setVisibility(View.GONE);
            CPUImageview.setVisibility(View.VISIBLE);
            humanImageview.setVisibility(View.INVISIBLE);
            roundEnded = true;
            roundEndedTime = currentTimeMillis();
        }
    };
    final int maxScore = 100;


    private void onGameEnd() {
        humanImageview.startAnimation(fade);
        CPUImageview.startAnimation(fade);
        if (score() > 0)
            textView.setText(R.string.won_string);
        else
            textView.setText(R.string.lost_string);
        Typeface aldrich = ResourcesCompat.getFont(this, R.font.aldrich);
        textView.setTypeface(aldrich);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
        round.postDelayed(new Runnable() {
            @Override
            public void run() {
                humanImageview.setVisibility(View.GONE);
                CPUImageview.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        finish();
                    }
                });

            }
        }, countDownTime);
    }

    /**
     * This is a callback after everyone has made up their choices
     * It must update the score and let the user continue playing
     */
    private void onChoicesMade() {
        int total = CPU + human;
        if (total % 2 == 1)
            scoreCPU += total;
        else
            scoreHuman += total;
        ((TextView) findViewById(R.id.score_CPU)).setText("" + scoreCPU);
        ((TextView) findViewById(R.id.score_human)).setText("" + scoreHuman);
        ((TextView) findViewById(R.id.total_score)).setText("" + score());
        humanImageview.startAnimation(fade);
        CPUImageview.startAnimation(fade);
        if (java.lang.Math.abs(score()) >= maxScore) {
            onGameEnd();
        }
        round.postDelayed(new Runnable() {
            @Override
            public void run() {
                humanImageview.setVisibility(View.GONE);
                CPUImageview.setImageResource(R.drawable.ic_continue);
                CPUImageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playRound();
                    }
                });

            }
        }, countDownTime);
    }

    private final Context context = this;
    private final View.OnClickListener onButtonPressed = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isPlaying && !roundEnded) {
                Toast toast = Toast.makeText(context, R.string.wait_string, Toast.LENGTH_SHORT);
                toast.show();
                // reset the round
                playRound();
            } else if (isPlaying && roundEnded) {
                long clickTime = currentTimeMillis();
                if (clickTime - roundEndedTime > maxDelay) {
                    Toast toast = Toast.makeText(context, "Don't cheat. Resetting round", Toast.LENGTH_SHORT);
                    toast.show();
                    // reset the round
                    playRound();
                } else {
                    // the player is no longer in the game, because he made up his choice
                    isPlaying = false;
                    ++nRounds;
                    switch (v.getId()) {
                        case R.id.one_button:
                            ++ones;
                            humanImageview.setImageResource(R.drawable.one_finger);
                            humanImageview.setVisibility(View.VISIBLE);
                            human = 1;

                            break;
                        case R.id.two_button:
                            ++twos;
                            humanImageview.setImageResource(R.drawable.two_fingers);
                            humanImageview.setVisibility(View.VISIBLE);
                            human = 2;
                            break;
                    }
                    clicked = true;
                    onChoicesMade();
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        fade = new AlphaAnimation(1.0f, 0.0f);
        fade.setDuration(countDownTime);
        textView = (TextView) findViewById(R.id.round_textview);
        CPUImageview = (ImageView) findViewById(R.id.cpu_imageview);
        humanImageview = (ImageView) findViewById(R.id.human_imageview);
        ImageView one = (ImageView) findViewById(R.id.one_button);
        ImageView two = (ImageView) findViewById(R.id.two_button);
        one.setOnClickListener(onButtonPressed);
        two.setOnClickListener(onButtonPressed);
        round = new Handler();
        playRound();
    }
    private int score() {
        return scoreHuman - scoreCPU;
    }


    public void playRound() {
        round.removeCallbacks(countDown);
        round.removeCallbacks(onRoundEnd);
        countDownDigit = 3;
        isPlaying = true;
        roundEnded = false;
        clicked = false;

        CPUImageview.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);

        // this loop does the countdown
        for (int i = 0; i < 3; ++i) {
            round.postDelayed(countDown, countDownTime * i);
        }
        CPU = getMove();
        int CPUImage = 1;
        switch (CPU) {
            case 1:
                CPUImage = R.drawable.one_finger;
                break;
            case 2:
                CPUImage = R.drawable.two_fingers;
                break;
        }
        CPUImageview.setImageResource(CPUImage);
        round.postDelayed(onRoundEnd, countDownTime * 3);
    }
    int getMove() {
        int n = random.nextInt(12);
        if (n < 5)
            return 2;
        else return 1;
    }
}
