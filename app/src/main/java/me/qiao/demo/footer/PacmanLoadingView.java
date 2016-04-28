package me.qiao.demo.footer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Qiao on 2016/4/26.
 * function：
 */
public class PacmanLoadingView extends View{
    private Paint mPaint;

    private float translateX;
    private int alpha;
    private float degrees;

    private ValueAnimator rotateAnim,ciclerAnim;

    private float x,y,width;
    private RectF rectF;

    public PacmanLoadingView(Context context) {
        super(context);

        mPaint=new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize;

        Resources r = Resources.getSystem();
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed) {
            x = getWidth() / 2;
            y = getHeight() / 2;
            width = Math.min(x, y);
            rectF=new RectF(-width/1.7f,-width/1.7f,width/1.7f,width/1.7f);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPacman(canvas,mPaint);
        drawCircle(canvas,mPaint);
    }

    private void drawPacman(Canvas canvas,Paint paint){
        paint.setAlpha(255);

        canvas.save();
        canvas.translate(x, y);
        canvas.rotate(degrees);
        canvas.drawArc(rectF, 0, 270, true, paint);
        canvas.restore();

        canvas.save();
        canvas.translate(x, y);
        canvas.rotate(-degrees);
        canvas.drawArc(rectF,90,270,true,paint);
        canvas.restore();
    }


    private void drawCircle(Canvas canvas, Paint paint) {
        paint.setAlpha(alpha);
        canvas.drawCircle(translateX, y, width/4, paint);
    }

    public void createAnimation() {
        ciclerAnim=ValueAnimator.ofFloat(1,0);
        ciclerAnim.setDuration(650);
        ciclerAnim.setInterpolator(new LinearInterpolator());
        ciclerAnim.setRepeatCount(-1);
        ciclerAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ratio = (float) animation.getAnimatedValue();
                translateX = x + width*2*ratio;
                alpha = (int)(122*(1+ratio));
                postInvalidate();
            }
        });
        ciclerAnim.start();

        rotateAnim=ValueAnimator.ofFloat(0, 45, 0);
        rotateAnim.setDuration(650);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degrees = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        rotateAnim.start();
    }

    public void startAnim(){
        if(rotateAnim==null) {
            createAnimation();
        }else {
            ciclerAnim.start();
            rotateAnim.start();
        }
    }

    public void stopAnim(){
        ciclerAnim.cancel();
        rotateAnim.cancel();
    }
}
