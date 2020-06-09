package com.example.android.jailpoker.play;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.android.jailpoker.R;

import static java.lang.System.currentTimeMillis;

public class PlayView extends AppCompatActivity {

    private PlayPresenter playPresenter;
    private final int countDownTime = 500;
    private Animation fade;
    private TextView textView;
    private ImageView CPUImageview;
    private ImageView humanImageview;
    private int countDownDigit;
    private Handler round;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        fade = new AlphaAnimation(1.0f, 0.0f);
        fade.setDuration(countDownTime);
        textView = (TextView) findViewById(R.id.round_textview);
        CPUImageview = (ImageView) findViewById(R.id.cpu_imageview);
        humanImageview = (ImageView) findViewById(R.id.human_imageview);
        round = new Handler();
        ImageView one = (ImageView) findViewById(R.id.one_button);
        ImageView two = (ImageView) findViewById(R.id.two_button);
        round = new Handler();
        playPresenter = new PlayPresenter(this, round, countDownTime);
        one.setOnClickListener(playPresenter.onButtonPressed);
        two.setOnClickListener(playPresenter.onButtonPressed);
        playPresenter.viewIsReady();

    }

    private final Runnable countDown = new Runnable() {
        @Override
        public void run() {
            textView.setText(Integer.toString(countDownDigit));
            textView.startAnimation(fade);
            --countDownDigit;
        }
    };

    public void displayChoices() {
        textView.setVisibility(View.GONE);
        CPUImageview.setVisibility(View.VISIBLE);
        humanImageview.setVisibility(View.INVISIBLE);
    };

    public void gameOverMessage(int resId) {
        String text = getString(resId);
        humanImageview.startAnimation(fade);
        CPUImageview.startAnimation(fade);
        textView.setText(text);
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
    public void displayScores(int scoreCPU, int scoreHuman, int scoreTotal) {
        ((TextView) findViewById(R.id.score_CPU)).setText(Integer.toString(scoreCPU));
        ((TextView) findViewById(R.id.score_human)).setText(Integer.toString(scoreHuman));
        ((TextView) findViewById(R.id.total_score)).setText(Integer.toString(scoreTotal));
        humanImageview.startAnimation(fade);
        CPUImageview.startAnimation(fade);
    }

    public void continuePlaying() {
        humanImageview.setVisibility(View.GONE);
        CPUImageview.setImageResource(R.drawable.ic_continue);

        CPUImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPresenter.viewIsReady();
            }
        });

    }

    public void makeToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void setHumanChoice(int resId) {
        humanImageview.setImageResource(resId);
        humanImageview.setVisibility(View.VISIBLE);
    }

    public void displayCountDown() {
        round.removeCallbacks(countDown);
        countDownDigit = 3;
        CPUImageview.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);

        // this loop does the countdown
        for (int i = 0; i < 3; ++i) {
            round.postDelayed(countDown, countDownTime * i);
        }

    }

    public void setCPUImage(int resId) {
        CPUImageview.setImageResource(resId);
    }
}
