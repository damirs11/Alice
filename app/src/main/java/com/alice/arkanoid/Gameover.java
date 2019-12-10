package com.alice.arkanoid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alexlopezramos.arkanoid.R;
import com.alice.arkanoid.View.GameView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class Gameover extends AppCompatActivity {

    private Integer CUR_SCORE = 0;
    private Integer HIGH_SCORE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_gameover);

        initComponents();
    }

    private void initComponents() {
        TextView textViewCurScore = findViewById(R.id.currentScore);
        TextView textViewHighScore = findViewById(R.id.highScore);
        Button btnStart = findViewById(R.id.btnStart);

        SharedPreferences sharedPreferences = getSharedPreferences("APP_PREFERENCES", MODE_PRIVATE);

        CUR_SCORE = getIntent().getIntExtra("Score", 0);
        HIGH_SCORE = sharedPreferences.getInt("HighScore", 0);

        if(HIGH_SCORE <= CUR_SCORE) {
            HIGH_SCORE = CUR_SCORE;

            sharedPreferences.edit().putInt("HighScore", HIGH_SCORE);
            sharedPreferences.edit().commit();
        }

        textViewCurScore.append(CUR_SCORE.toString());
        textViewHighScore.append(HIGH_SCORE.toString());

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GameView.class);
                startActivity(i);
            }
        });
    }
}
