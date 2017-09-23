package com.zia.waveview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by zia on 2017/9/20.
 */

public class WaveView extends android.support.v7.widget.AppCompatTextView implements SensorEventListener{

    private Paint paint = new Paint();
    private Paint paint1 = new Paint();
    private Path path = new Path();
    private Path path1 = new Path();
    private float startX,startY,endX,endY;//左右两端高度位置
    private float assistX,assistY;//第一个波浪控制点
    private float assistX1,assistY1;//第儿个波浪控制点
    private final int speed = 8;//第一个波浪速度
    private final int speed1 = 7;//第二个波浪速度
    private boolean isRight = true,isRight1 = true;//控制上下移动
    private boolean isCenterRight = true;
    private boolean isUp = true,isUp1 = true;
    private boolean isInited = false;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float py = 0;
    private float centerPy = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) return;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            py =  event.values[0] * 2;
            Log.e("qwe", String.valueOf(py));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setColor(Color.parseColor("#aaCAE1FF"));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint1.setColor(Color.parseColor("#aaBCD2EE"));
        paint1.setStrokeWidth(10);
        paint1.setStyle(Paint.Style.FILL_AND_STROKE);
        paint1.setAntiAlias(true);
        paint1.setFilterBitmap(true);
        //重力传感器
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        if (mSensorManager != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(py,getWidth()/2,getHeight()/2);
        if(!isInited){
            startX = -100;startY = getHeight() / 2;
            endX = getWidth() +100;endY = getHeight() / 2;
            assistX = assistX1 = getWidth() / 4;assistY = assistY1 = getHeight() / 2;
            isInited = true;
        }
        updataPath(path,assistX,assistY);
        updataPath(path1,assistX1,assistY1);
        canvas.drawPath(path,paint);
        canvas.drawPath(path1,paint1);
        changeAssistPoint();
        postInvalidate();
    }

    /**
     * 改变控制点
     * 这里控制点我设置为绕一个椭圆运动
     */
    private void changeAssistPoint(){
        //设置振幅
        float maxHeight = getHeight()*2/5;
        float minHeight = getHeight()*3/5;
        //设置左右移动距离
        float maxWidth = getWidth()*3/4;
        float minWidth = getWidth()/4;
        if(isRight){
            assistX += speed;
            if(assistX >= maxWidth) isRight = false;
        }else{
            assistX -= speed;
            if(assistX <= minWidth) isRight = true;
        }
        if(isUp){
            assistY += (speed - 4);
            if(assistY >= maxHeight) isUp = false;
        }else{
            assistY -= (speed - 4);
            if(assistY <= minHeight) isUp = true;
        }
        if(isRight1){
            assistX1 += speed1;
            if(assistX1 >= maxWidth) isRight1 = false;
        }else{
            assistX1 -= speed1;
            if(assistX1 <= minWidth) isRight1 = true;
        }
        if(isUp1){
            assistY1 += (speed1 - 4);
            if(assistY1 >= maxHeight) isUp1 = false;
        }else{
            assistY1 -= (speed1 - 4);
            if(assistY1 <= minHeight) isUp1 = true;
        }
    }

    private void updataPath(Path path,float assistX,float assistY){
        path.reset();
        path.moveTo(startX,startY);
        //path.quadTo(assistX,assistY,endX,endY);
        path.cubicTo(assistX,assistY,getOpX(assistX),getOpY(assistY),endX,endY);
        //区域为两个屏幕大小，避免旋转画布时留出空白
        path.lineTo(getWidth()*2,getHeight()*2);
        path.lineTo(-getWidth(),getHeight()*2);
        path.close();
    }

    /**
     * 获取屏幕中点对称的x
     * @param x
     * @return
     */
    private float getOpX(float x){
        float middleX = getWidth()/2 + centerPy;
        //增加一个变化的中心偏移量
        if(isCenterRight){
            centerPy = centerPy + 1;
            if(centerPy >= 80) isCenterRight = false;
        }else{
            centerPy = centerPy - 1;
            if(centerPy <= -80) isCenterRight = true;
        }
        if(x > middleX){
            return middleX - (x - middleX);
        }
        else if(x < middleX){
            return middleX + (middleX - x);
        }
        else return x;
    }

    private float getOpY(float y){
        float middleY = getHeight()/2 + centerPy;
        if(y > middleY){
            return middleY - (y - middleY);
        }
        else if(y < middleY){
            return middleY + (middleY - y);
        }else return y;
    }

}
