package com.example.android.jailpoker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.android.jailpoker.play.PlayPresenter;
import com.example.android.jailpoker.play.PlayView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtonActions();
    }

    private void setButtonActions() {
        Button playButton = (Button) findViewById(R.id.play_button);
        Button rulesButton = (Button) findViewById(R.id.rules_button);

        rulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DialogFragment rulesFragment = new Rules();
                rulesFragment.show(fm, "rules");
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayView.class);
                startActivity(intent);
            }
        });
    }
}
