package com.example.android.jailpoker.play;

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

import com.example.android.jailpoker.R;

import java.util.Random;

import static java.lang.System.currentTimeMillis;

public class PlayPresenter {

    PlayPresenter(PlayView playView, Handler round, int countDownTime) {
        this.round = round;
        this.countDownTime = countDownTime;
        this.playView = playView;
    }
    public void viewIsReady() {
        playRound();
    }
    private final int maxDelay = 300;
    private int countDownTime;
    private boolean clicked;
    private final Random random = new Random();

    int nRounds = 0, ones, twos;
    private int scoreHuman = 0, scoreCPU = 0;
    boolean isPlaying = false, roundEnded = false;
    long roundEndedTime;
    private PlayView playView;

    public Handler round;

    // moves
    int CPU, human;
    // these two Runnables are used to handle events that occur during each round

    private final Runnable onRoundEnd = new Runnable() {
        @Override
        public void run() {
            playView.displayChoices();
            roundEnded = true;
            roundEndedTime = currentTimeMillis();
        }
    };
    final int maxScore = 100;


    private void onGameEnd() {
        if (score() > 0)
            playView.gameOverMessage(R.string.won_string);
        else
            playView.gameOverMessage(R.string.lost_string);
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
        playView.displayScores(scoreCPU, scoreHuman, score());
        if (java.lang.Math.abs(score()) >= maxScore) {
            onGameEnd();
        }
        round.postDelayed(new Runnable() {
            @Override
            public void run() {
                playView.continuePlaying();
            }
        }, countDownTime);
    }

    public final View.OnClickListener onButtonPressed = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isPlaying && !roundEnded) {
                playView.makeToast("Wait for the countdown");
                // reset the round
                playRound();
            } else if (isPlaying && roundEnded) {
                long clickTime = currentTimeMillis();
                if (clickTime - roundEndedTime > maxDelay) {
                    playView.makeToast( "Don't cheat. Resetting round");
                    // reset the round
                    playRound();
                } else {
                    // the player is no longer in the game, because he made up his choice
                    isPlaying = false;
                    ++nRounds;
                    switch (v.getId()) {
                        case R.id.one_button:
                            ++ones;
                            playView.setHumanChoice(R.drawable.one_finger);
                            human = 1;
                            break;
                        case R.id.two_button:
                            ++twos;
                            playView.setHumanChoice(R.drawable.two_fingers);
                            human = 2;
                            break;
                    }
                    clicked = true;
                    onChoicesMade();
                }
            }
        }

    };


    private int score() {
        return scoreHuman - scoreCPU;
    }


    public void playRound() {
        round.removeCallbacks(onRoundEnd);
        isPlaying = true;
        roundEnded = false;
        clicked = false;
        playView.displayCountDown();
        CPU = getMove();
        playView.setCPUImage(CPU == 1 ? R.drawable.one_finger : R.drawable.two_fingers);
        round.postDelayed(onRoundEnd, countDownTime * 3);
    }

    // This is the only function that has irrelevant logic, but it's not worth creating a model for it
    int getMove() {
        int n = random.nextInt(12);
        if (n < 5)
            return 2;
        else return 1;
    }
}
