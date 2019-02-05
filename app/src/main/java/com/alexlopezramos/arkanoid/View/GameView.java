package com.alexlopezramos.arkanoid.View;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.alexlopezramos.arkanoid.Objects.Ball;
import com.alexlopezramos.arkanoid.Objects.Brick;
import com.alexlopezramos.arkanoid.Objects.Paddle;
import com.alexlopezramos.arkanoid.R;

import java.io.IOException;

public class GameView extends AppCompatActivity {

    BreakoutView breakoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);
    }

    //SurfaceView
    class BreakoutView extends SurfaceView implements Runnable {

        //Thread
        private Thread thread;
        private SurfaceHolder surfHolder;

        //Canvas and Screen
        private Canvas canvas;
        private Paint paint;
        private int screenX;
        private int screenY;

        //Game Booleans
        private boolean playing;
        private boolean paused = true;
        private boolean win = false;
        private long fps;
        private long timeThisFrame;

        //Player
        private Paddle paddle;
        private Bitmap bmpPlayer;
        private Rect src, dst;

        //Ball
        private Ball ball;

        //Bricks
        private Brick[] bricks = new Brick[200];
        private int numBricks;
        private int numRows;
        private int numBricksPerRow;

        //Score
        private int score = 0;

        //Lives
        private int lives = 3;

        //Sound FX
        private SoundPool soundPool;
        private int beep1ID = -1;
        private int beep2ID = -1;
        private int beep3ID = -1;
        private int loseLifeID = -1;
        private int explodeID = -1;

        public BreakoutView(Context context) {
            super(context);

            //Canvas and Screen
            surfHolder = getHolder();
            paint = new Paint();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenX = size.x;
            screenY = size.y;

            //Player
            bmpPlayer = BitmapFactory.decodeResource(getResources(), R.drawable.player);
            paddle = new Paddle(screenX, screenY, bmpPlayer);

            //Ball
            ball = new Ball(screenX, screenY, paddle.getHeight());

            //Bricks
            numRows = 4;
            numBricksPerRow = 8;

            //Sound FX
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            try {
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                descriptor = assetManager.openFd("beep1.ogg");
                beep1ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep2.ogg");
                beep2ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("beep3.ogg");
                beep3ID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("loseLife.ogg");
                loseLifeID = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("explode.ogg");
                explodeID = soundPool.load(descriptor, 0);
            } catch (IOException e) {
                Log.e("error", "failed to load sound files");
            }

            createBricksAndRestart();

        }

        public void createBricksAndRestart() {

            //Reset Ball
            ball.reset(screenX, screenY, paddle.getHeight());

            int brickWidth = screenX / numBricksPerRow;
            int brickHeight = screenY / 20;

            //Create Wall
            numBricks = 0;
            for (int column = 0; column < numBricksPerRow; column++) {
                for (int row = 0; row < numRows; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }

            //Game Over
            if(lives <= 0) {
                score = 0;
                lives = 3;
            }

            if(win) {
                score = 0;
                lives = 3;
            }
        }

        @Override
        public void run() {
            while (playing) {
                long startFrameTime = System.currentTimeMillis();

                if (!paused)
                    update();

                draw();
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }
            }
        }

        public void update() {
            //Mover Player
            paddle.update(fps, screenX);

            //Movimiento Ball
            ball.update(fps);

            //Ball choca con Brick
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    if (Rect.intersects(bricks[i].getRect(), ball.getRect())) {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score = score + 10;
                        soundPool.play(explodeID, 1, 1, 0, 0, 1);
                    }
                }
            }

            //Ball choca con Player
            if (Rect.intersects(paddle.getRect(), ball.getRect())) {
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRect().top - 40);
                soundPool.play(beep1ID, 1, 1, 0, 0, 1);
            }

            //Ball choca con Screen Bottom
            if (ball.getRect().bottom + 20 > screenY) {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 30);

                // Lose a life
                lives--;
                soundPool.play(loseLifeID, 1, 1, 0, 0, 1);

                if (lives <= 0) {
                    paused = true;
                    win = false;
                    createBricksAndRestart();
                }
            }

            //Ball choca con Screen Top
            if (ball.getRect().top < 0) {
                ball.reverseYVelocity();
                ball.clearObstacleY(30);

                soundPool.play(beep2ID, 1, 1, 0, 0, 1);
            }

            //Ball choca con Screen Left
            if (ball.getRect().left < 0) {
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            //Ball choca con Screen Right
            if (ball.getRect().right > screenX) {

                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 30);

                soundPool.play(beep3ID, 1, 1, 0, 0, 1);
            }

            //Pause si gana
            if(score == numBricks * 10) {
                paused = true;
                win = true;
                createBricksAndRestart();
            }
        }

        public void draw() {

            if (surfHolder.getSurface().isValid()) {
                canvas = surfHolder.lockCanvas();

                //Color de fondo
                canvas.drawColor(Color.BLACK);

                //Color Ball
                paint.setColor(Color.argb(255, 255, 255, 255));

                //Player
                src = new Rect(0, 0, bmpPlayer.getWidth(), bmpPlayer.getHeight()); //BMP Frame Size
                dst = new Rect(paddle.getRect()); //Screen Frame Size
                canvas.drawBitmap(bmpPlayer, src, dst, null);

                //Ball
                canvas.drawRect(ball.getRect(), paint);

                //Color Bricks
                //paint.setColor(Color.RED);

                //Bricks
                for (int i = 0; i < numBricks; i++) {
                    switch(i) {
                        case 0: case 4: case 8: case 12:
                        case 16: case 20: case 24: case 28:
                            paint.setColor(Color.RED);
                            break;
                        case 1: case 5: case 9: case 13:
                        case 17: case 21: case 25: case 29:
                            paint.setColor(Color.BLUE);
                            break;
                        case 2: case 6: case 10: case 14:
                        case 18: case 22: case 26: case 30:
                            paint.setColor(Color.GREEN);
                            break;
                        case 3: case 7: case 11: case 15:
                        case 19: case 23: case 27: case 31:
                            paint.setColor(Color.YELLOW);
                            break;
                    }
                    if (bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                //Color letras
                paint.setColor(Color.WHITE);

                //Score
                paint.setTextSize(40);
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("Score: " + score + "   Lives: " + lives, 10, 50, paint);

                //Win
                if(score == numBricks * 10 && win && paused) {
                    paint.setTextSize(90);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText("¡HAS GANADO!", screenX / 2, screenY / 2, paint);
                }

                //Fail
                if(lives <= 0 && !win && paused) {
                    paint.setTextSize(90);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText("¡HAS PERDIDO!", screenX / 2, screenY / 2, paint);
                }

                surfHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause() {
            playing = false;
            try {
                thread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        public void resume() {
            playing = true;
            thread = new Thread(this);
            thread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    paused = false;
                    win = false;
                    if (motionEvent.getX() > screenX / 2) {
                        paddle.setMovementState(paddle.RIGHT);
                    } else {
                        paddle.setMovementState(paddle.LEFT);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    paddle.setMovementState(paddle.STOPPED);
                    break;
            }
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        breakoutView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        breakoutView.pause();
    }
}
