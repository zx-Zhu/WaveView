package com.zia.waveview;

import android.animation.TypeEvaluator;

/**
 * Created by zia on 2017/9/20.
 */

public class PointEvaluator implements TypeEvaluator {
    @Override
    public Object evaluate(float v, Object o, Object t1) {
        Point startPoint = (Point) o;
        Point endPoint = (Point) t1;
        float x = startPoint.getX() + v*(endPoint.getX() - startPoint.getX());
        float y = startPoint.getY() + v*(endPoint.getY() - startPoint.getY());
        Point point = new Point();
        point.setX(x);
        point.setY(y);
        return point;
    }
}
