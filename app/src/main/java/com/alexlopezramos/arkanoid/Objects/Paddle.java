package com.alexlopezramos.arkanoid.Objects;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class Paddle {

    private Rect rect;
    private int width;
    private int height;
    private int x;
    private int y;
    private float paddleSpeed;

    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    private int paddleMoving = STOPPED;

    public Paddle(int screenX, int screenY, Bitmap bmpPlayer){
        this.width = bmpPlayer.getWidth();
        this.height = bmpPlayer.getHeight();
        this.x = (screenX / 2) - (width / 2);
        this.y = screenY - height;

        this.rect = new Rect(x, y, x + width, y + height);
        this.paddleSpeed = 350;
    }

    public Rect getRect(){
        return rect;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setMovementState(int state){
        paddleMoving = state;
    }

    public void update(long fps, float screenX){

        x = (int)(screenX - paddleSpeed / fps);

        rect.left = x;
        rect.right = x + width;
    }
}
