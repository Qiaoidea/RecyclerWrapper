package me.qiao.demo.header;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.qiao.demo.R;
import me.qiao.wrapper.IRefreshHeader;

/**
 * Created by Qiao on 2016/4/23.
 * function：
 */
public class RefreshHeader extends LinearLayout implements IRefreshHeader{

    private final String pullToRefresh = "pull to refresh..";
    private final String releaseToRefresh = "release to refresh..";
    private final String refreshing = "refreshing...";

    private ProgressBar progressBar;
    private TextView textView;
    private ImageView imageView;

    public boolean isEnughToRefresh;

    public RefreshHeader(Context context) {
        super(context);
        initialize(context);
    }

    public RefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public RefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }
    
    private  void initialize(Context context){
        setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));


        progressBar = new ProgressBar(context);
        progressBar.setVisibility(GONE);
        LinearLayout.LayoutParams pllp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        pllp.rightMargin = 30;
        pllp.gravity = Gravity.CENTER_VERTICAL;
        addView(progressBar,pllp);

        imageView = new ImageView(context);
        imageView.setImageResource(R.mipmap.ic_pulltorefresh_arrow);
        LinearLayout.LayoutParams illp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        illp.rightMargin = 30;
        addView(imageView,illp);

        textView = new TextView(context);
        textView.setText("pull to refresh..");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);

        addView(textView);

        setPadding(0,0,0,10);
        final ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize ;

        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onVisibleScopeChanged(int visibleHeight){
//        Log.e("onVisibleScopeChanged", "visibleHeight:"+visibleHeight);
//        view.setText("isRedy:"+(visibleHeight>getHeight()/2));

        final int translateY = (textView.getHeight()-visibleHeight)/2;
        ViewCompat.setTranslationY(textView,translateY);
        ViewCompat.setTranslationY(imageView,translateY);

        final int halfHeight = getHeight()/2;
        if(visibleHeight>=halfHeight && pullToRefresh.equals(textView.getText())){
            textView.setText(releaseToRefresh);
            isEnughToRefresh = true;
        }else if(visibleHeight<halfHeight && releaseToRefresh.equals(textView.getText())){
            textView.setText(pullToRefresh);
            isEnughToRefresh = false;
        }

        if(Math.abs(visibleHeight-halfHeight)<=30) {
            final float ratio = 3 * (visibleHeight+30 - halfHeight);
            ViewCompat.setRotation(imageView, ratio);
        }else if(isEnughToRefresh && ViewCompat.getRotation(imageView)!=180){
            ViewCompat.setRotation(imageView, 180);
        }else if(!isEnughToRefresh && ViewCompat.getRotation(imageView)!=0){
            ViewCompat.setRotation(imageView, 0);
        }
    }

    @Override
    public boolean isEnughToRefresh() {
        return isEnughToRefresh;
    }

    @Override
    public void onRefresh() {
        textView.setText(refreshing);
        progressBar.setVisibility(VISIBLE);
        imageView.setVisibility(GONE);
    }

    @Override
    public void onComplete() {
        textView.setText(pullToRefresh);
        progressBar.setVisibility(GONE);
        imageView.setVisibility(VISIBLE);
        ViewCompat.setRotation(imageView, 0);
    }
}
