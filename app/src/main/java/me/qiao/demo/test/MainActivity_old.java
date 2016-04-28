package me.qiao.demo.test;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import me.qiao.demo.R;
import me.qiao.demo.sample.DividerItemDecoration;
import me.qiao.demo.header.RefreshHeader;

public class MainActivity_old extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private RefreshHeader mRefreshHeader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView = new RecyclerView(this);
        setContentView(mRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(mOnScrollerListener);

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mAttachListener);
    }

    private int visibleHeaderHeight;
    RecyclerView.OnScrollListener mOnScrollerListener = new RecyclerView.OnScrollListener() {
        private final int REFRESH_STATE_SETTING = -1;
        private final int REFRESH_STATE_IDEL = 0;
        private final int REFRESH_STATE_REFRESING = 2;

        private int scrollY =0;
        private int state = REFRESH_STATE_IDEL;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if(newState == RecyclerView.SCROLL_STATE_IDLE){
                if(isHeaderShown()){
                    if(visibleHeaderHeight>=mRefreshHeader.getHeight()/2) {
                        mRecyclerView.smoothScrollBy(0,visibleHeaderHeight-mRefreshHeader.getHeight());
                        state = REFRESH_STATE_REFRESING;
                        onRefresh();
                    }else{
                        state = REFRESH_STATE_SETTING;
                        mRecyclerView.smoothScrollBy(0,visibleHeaderHeight);
                    }
                }
//                Log.e("Main_isHeaderShown", " "+isHeaderShown());
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if(isHeaderShown()){
                final int invisibleHeight = caculate(scrollY +dy,0,mRefreshHeader.getHeight());
                visibleHeaderHeight = mRefreshHeader.getHeight() - invisibleHeight;
                mRefreshHeader.onVisibleHeightChanged(visibleHeaderHeight);
            }
            scrollY += dy;
//            Log.i("MainActivity_onScrolled", " scrollY:"+scrollY);
        }

        private boolean isHeaderShown(){
            return null!=mRefreshHeader && scrollY <= mRefreshHeader.getHeight();
        }


        private int caculate(int x,int min,int max){
            return Math.max(min, Math.min(x, max) );
        }

    };

    ViewTreeObserver.OnGlobalLayoutListener mAttachListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }else {
                mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            visibleHeaderHeight = mRefreshHeader.getHeight();
            onRefresh();
        }
    };

    private void onRefresh(){
        Log.e("MainActivity_onRefresh", " .........");
        onRefeshComplete();
    }

    private void onRefeshComplete(){
        mRecyclerView.postDelayed(delayDismiss,1000);
    }
    private Runnable delayDismiss = new Runnable() {
        @Override
        public void run() {
            mRecyclerView.smoothScrollBy(0,visibleHeaderHeight);
        }
    };

    RecyclerView.Adapter mAdapter = new RecyclerView.Adapter() {
        public final static  int TYPE_REFRESH_HEADER = -1;
        public final static  int TYPE_ITEM = -2;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case TYPE_REFRESH_HEADER:
                    return new SimpleViewHolder(mRefreshHeader = new RefreshHeader(parent.getContext()));
                default:
                    return new ImageHolder(LayoutInflater.from(MainActivity_old.this).inflate(R.layout.list_item_image,parent,false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof  ImageHolder)
                ((ImageHolder)holder).bindData("item"+position);
        }

        @Override
        public int getItemCount() {
            return 31;
        }

        @Override
        public int getItemViewType(int position) {
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            return TYPE_ITEM;
        }

        public boolean isRefreshHeader(int position) {
            return  position == 0;
        }

        class SimpleViewHolder extends RecyclerView.ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    };

    class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener,Checkable {
        private ImageView imageView;
        private TextView textView;
        private boolean isChecked;
        private String name;

        public ImageHolder(View view){
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image);
            textView = (TextView)view.findViewById(R.id.text);
            view.setOnClickListener(this);
        }

        public void bindData(String name){
            this.name = name;

            textView.setText(name);
        }

        @Override
        public void onClick(View v) {
            toggle();
        }

        @Override
        public void setChecked(boolean checked) {
            if(isChecked!=checked){
                isChecked = checked;
            }
        }

        @Override
        public boolean isChecked() {
            return isChecked;
        }

        @Override
        public void toggle() {
            setChecked(!isChecked);
        }
    }
}
