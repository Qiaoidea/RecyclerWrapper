package me.qiao.wrapper;

import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.List;

/**
 * Created by Qiao on 2016/4/26.
 * function：
 */
public class RecyclerWrapper{
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    /**
     * 下拉刷新相关
     */
    private View mRefreshHeader;
    private IRefreshHeader iRefreshHeader;
    private int visibleHeaderHeight;
    private onRefreshListener onRefreshListener;
    private boolean isRefreshing;

    /**
     * 加载更多相关
     */
    private View mLoadMoreFooter;
    private onLoadMoreListener onLoadMoreListener;
    private QAdapter mAdapter;

    public static RecyclerWrapper bindOn(RecyclerView recyclerView){
        return new RecyclerWrapper(recyclerView);
    }

    private RecyclerWrapper(RecyclerView recyclerView){
        this.mRecyclerView = recyclerView;

        mRecyclerView.addOnScrollListener(mOnScrollerListener);

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mAttachListener);

    }
    
    public RecyclerWrapper layoutBy(RecyclerView.LayoutManager layoutManager){
        this.mLayoutManager = layoutManager;
        return this;
    }

    public RecyclerWrapper withRefreshHeader(View header,onRefreshListener onRefreshListener){
        mRefreshHeader = header;
        this.onRefreshListener = onRefreshListener;
        if(header instanceof IRefreshHeader){
            iRefreshHeader = (IRefreshHeader)header;
        }
        return this;
    }

    public RecyclerWrapper withLoadMoreFooter(View footer,onLoadMoreListener onLoadMoreListener){
        mLoadMoreFooter = footer;
        this.onLoadMoreListener = onLoadMoreListener;
        return this;
    }

    public RecyclerWrapper setAdapter(RecyclerView.Adapter adapter){
        mAdapter = new QAdapter(adapter);
        return this;
    }

    public void build(){
        if(mRecyclerView==null){
            throw new IllegalStateException("please bindOn a RecyclerView before this .. ");
        }else{
            if(mLayoutManager == null){
                mLayoutManager = mRecyclerView.getLayoutManager();
                if(mLayoutManager==null){
                    throw new IllegalStateException("please stepup RecyclerView withs a LayoutManager before this .. ");
                }
            }else{
                mRecyclerView.setLayoutManager(mLayoutManager);
            }
            wrapSpanLook();

            if(mAdapter==null){
                RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                if(adapter!=null) {
                    mRecyclerView.setAdapter(mAdapter = new QAdapter(adapter));
                }
            }else {
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    /**
     *
     *保持header 和footer占满行/列
     */
    private void wrapSpanLook(){
        if (mLayoutManager instanceof GridLayoutManager) {
            final SpanSizeLookupGridWrapper wrapperSpanSizeLookup = new SpanSizeLookupGridWrapper(
                    ((GridLayoutManager) mLayoutManager), mAdapter);
            ((GridLayoutManager) mLayoutManager).setSpanSizeLookup(wrapperSpanSizeLookup);
        } else if(mLayoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams lp = new StaggeredGridLayoutManager
                    .LayoutParams(StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                    StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
            lp.setFullSpan(true);
            if(null!=mRefreshHeader){
                mRefreshHeader.setLayoutParams(lp);
            }
            if(null!=mLoadMoreFooter){
                mLoadMoreFooter.setLayoutParams(lp);
            }
        }
    }

    private void onRefresh(){
        isRefreshing = true;
        if(null!=onRefreshListener){
            onRefreshListener.onRefresh(mRefreshCompleteCallback);
        }
        if(null!=iRefreshHeader)iRefreshHeader.onRefresh();
    }

    private Callbaclk mRefreshCompleteCallback = new Callbaclk() {
        @Override
        public void onComplete(List data) {
            mRecyclerView.smoothScrollBy(0,visibleHeaderHeight);
            isRefreshing = false;
            if(null!=iRefreshHeader)iRefreshHeader.onComplete();
        }
    };

    private Callbaclk mLoadMoreCompleteCallback = new Callbaclk() {
        @Override
        public void onComplete(List data) {
        }
    };

    /**
     * 滚动事件监听
     */
    RecyclerView.OnScrollListener mOnScrollerListener = new RecyclerView.OnScrollListener() {

        private int scrollY =0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if(newState == RecyclerView.SCROLL_STATE_IDLE){
                //如果当前正在刷新就不再次调用
                if(!isRefreshing && isHeaderShown()){
                    //当手势停止的时候如果HeaderView显示超过一半，视为下拉刷新
                    if(visibleHeaderHeight>=mRefreshHeader.getHeight()/2) {
                        mRecyclerView.smoothScrollBy(0,visibleHeaderHeight-mRefreshHeader.getHeight());
                        onRefresh();
                    }else{
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
                if(null!=iRefreshHeader)iRefreshHeader.onVisibleHeightChanged(visibleHeaderHeight);
            }
            scrollY += dy;
//            Log.i("MainActivity_onScrolled", " scrollY:"+scrollY);
        }

        /**
         * 头部header是否显示
         * @return
         */
        private boolean isHeaderShown(){
            return null!=mRefreshHeader && scrollY <= mRefreshHeader.getHeight();
        }

        /**
         * 计算中间值
         * @param x 当前x
         * @param min
         * @param max
         * @return
         */
        private int caculate(int x,int min,int max){
            return Math.max(min, Math.min(x, max) );
        }
    };

    /**
     * layout布局初始化时默认刷新
     */
    ViewTreeObserver.OnGlobalLayoutListener mAttachListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }else {
                mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }

            if(null!=mRefreshHeader) visibleHeaderHeight = mRefreshHeader.getHeight();

            onRefresh();
        }
    };

    /**
     * 适配新的Adapter
     */
    protected class QAdapter extends RecyclerView.Adapter{
        public final static  int TYPE_REFRESH_HEADER = -501;
        public final static  int TYPE_ITEM = -502;
        public final static  int TYPE_LOADMORE_FOOTER = -503;

        private RecyclerView.Adapter adapter;

        private QAdapter(RecyclerView.Adapter adapter){
            this.adapter = adapter;
            this.adapter.registerAdapterDataObserver(mDataObserver);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType){
                case TYPE_REFRESH_HEADER:
                    return new QViewHolder(mRefreshHeader);
                case TYPE_LOADMORE_FOOTER:
                    return new QViewHolder(mLoadMoreFooter);
                default:
                    return adapter.onCreateViewHolder(parent,viewType);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)){
                case TYPE_REFRESH_HEADER:
                    break;
                case TYPE_LOADMORE_FOOTER:
                    if(null!= onLoadMoreListener){
                        onLoadMoreListener.onLoadMore(mLoadMoreCompleteCallback);
                    }
                    break;
                default:
                    this.adapter.onBindViewHolder(holder,position);
            }
        }

        @Override
        public int getItemCount() {
            return adapter.getItemCount() +
                    (mRefreshHeader==null? 0:1) +
                    (mLoadMoreFooter==null? 0:1);
        }

        @Override
        public int getItemViewType(int position) {
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }else if(isLoadMoreFooter(position)){
                return TYPE_LOADMORE_FOOTER;
            }
            return TYPE_ITEM;
        }

        public boolean isRefreshHeader(int position) {
            return  null!=mRefreshHeader && position == 0;
        }

        public boolean isLoadMoreFooter(int position) {
            return  null!=mLoadMoreFooter && position == getItemCount()-1;
        }

        class QViewHolder extends RecyclerView.ViewHolder {
            public QViewHolder(View itemView) {
                super(itemView);
            }
        }

        private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart+(mRefreshHeader==null?0:1)
                        ,itemCount);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                notifyItemRangeChanged(positionStart+(mRefreshHeader==null?0:1), itemCount, payload);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart+(mRefreshHeader==null?0:1), itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                notifyItemMoved(positionStart+(mRefreshHeader==null?0:1), itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                notifyItemMoved(fromPosition+(mRefreshHeader==null?0:1), toPosition+(mRefreshHeader==null?0:1));
            }
        };
    }

    public interface onRefreshListener{
        void onRefresh(Callbaclk callbaclk);
    }

    public interface onLoadMoreListener{
        void onLoadMore(Callbaclk callbaclk);
    }

    public interface Callbaclk{
       void onComplete(List data);
    }
}
