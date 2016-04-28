package me.qiao.demo.header;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;

import me.qiao.demo.R;
import me.qiao.wrapper.IRefreshHeader;

/**
 * Created by Qiao on 2016/4/28.
 * function：
 */
public class HorizantalRefreshHeader extends View implements IRefreshHeader {
    private int visibleScope;
    private Paint mPaint;

    private float x,y,width;
    private RectF rectF;

    public HorizantalRefreshHeader(Context context) {
        super(context);

        mPaint=new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize;
        Resources r = Resources.getSystem();
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, r.getDisplayMetrics());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed) {
            x = getWidth() / 2;
            y = getHeight() / 2;
            width = Math.min(x/2, y/2);
            rectF=new RectF(-width/1.7f,-width/1.7f,width/1.7f,width/1.7f);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.colorPrimaryDark));

        canvas.save();
        canvas.translate(2*x-visibleScope/2,y);
        float sweepAngle = 360f*visibleScope/getWidth();
        canvas.drawArc(rectF,-180,sweepAngle,true,mPaint);
        canvas.restore();
    }

    @Override
    public void onVisibleScopeChanged(int visibleHeight) {
        this.visibleScope = visibleHeight;
        invalidate();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public boolean isEnughToRefresh() {
        return visibleScope>=x;
    }

    @Override
    public void onComplete() {

    }
}
