package com.example.tiennguyen.luanvannew.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.tiennguyen.luanvannew.MainActivity;
import com.example.tiennguyen.luanvannew.R;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundActivity extends AppCompatActivity {

    LinearLayout llBackground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);

        setAnimation();
        setTimer();
    }

    private void setAnimation() {
        llBackground = (LinearLayout) findViewById(R.id.background);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.bg_fade_in);
        llBackground.startAnimation(anim);
    }

    private void setTimer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(BackgroundActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, 3000);
    }
}
