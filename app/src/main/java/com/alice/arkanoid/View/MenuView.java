package com.alice.arkanoid.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.alexlopezramos.arkanoid.R;

public class MenuView extends AppCompatActivity {

    ImageView wallpaper, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_main);

        initComponents();
    }

    private void initComponents() {
        wallpaper = findViewById(R.id.wallpaper);
        title = findViewById(R.id.title);
        Button btn2 = findViewById(R.id.one);
        Button btn3 = findViewById(R.id.second);
        Button btn1 = findViewById(R.id.three);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GameView.class);
                i.putExtra("NUM_ROWS", 8);
                i.putExtra("NUM_BRICKS_PER_ROW", 8);
                startActivity(i);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GameView.class);
                i.putExtra("NUM_ROWS", 12);
                i.putExtra("NUM_BRICKS_PER_ROW", 12);
                startActivity(i);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GameView.class);
                i.putExtra("NUM_ROWS", 5);
                i.putExtra("NUM_BRICKS_PER_ROW", 5);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Do you really want to exit?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                moveTaskToBack(true);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Cancel button
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
