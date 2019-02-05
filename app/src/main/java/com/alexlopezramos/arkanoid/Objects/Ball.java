package com.alexlopezramos.arkanoid.Objects;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.Random;

public class Ball {

    private Rect rect;
    private int width;
    private int height;
    private int x;
    private int y;
    private float xVelocity;
    private float yVelocity;

    public Ball(int screenX, int screenY, float paddleHeight) {
        this.width = 20;
        this.height = 20;
        this.x = (screenX / 2) - (width / 2);
        this.y = (int)(screenY - paddleHeight - height - 5);
        this.xVelocity = 350;
        this.yVelocity = -700;

        this.rect = new Rect(x, y, x + width, y + height);
    }

    public Rect getRect() {
        return rect;
    }

    public void update(long fps) {
        rect.left = (int)(rect.left + (xVelocity / fps));
        rect.top = (int)(rect.top + (yVelocity / fps));
        rect.right = rect.left + width;
        rect.bottom = rect.top - height;
    }

    public void reverseYVelocity() {
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity() {
        xVelocity = -xVelocity;
    }

    public void setRandomXVelocity() {
        Random rnd = new Random();
        int number = rnd.nextInt(2);

        if(number == 0){
            reverseXVelocity();
        }
    }

    public void clearObstacleY(int y) {
        rect.bottom = y;
        rect.top = y - height;
    }

    public void clearObstacleX(int x){
        rect.left = x;
        rect.right = x + width;
    }

    public void reset(int screenX, int screenY, float paddleHeight) {
        rect.left = screenX / 2 - (width / 2);
        rect.top = (int)(screenY - height - paddleHeight - 5);
        rect.right = x + width;
        rect.bottom = y + height;
    }
}
