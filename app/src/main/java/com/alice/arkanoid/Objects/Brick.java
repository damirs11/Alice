package com.alice.arkanoid.Objects;

import android.graphics.Rect;

public class Brick {

    private Rect rect;
    private boolean isVisible;

    public Brick(int row, int column, int width, int height){

        isVisible = true;
        int padding = 5;

        rect = new Rect(column * width + padding,
                row * height + padding + 80,
                column * width + width - padding,
                row * height + height - padding + 80);
    }

    public Rect getRect(){
        return this.rect;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }
}
