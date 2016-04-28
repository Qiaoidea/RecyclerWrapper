package me.qiao.wrapper;

/**
 * Created by Qiao on 2016/4/23.
 * function：
 */
public interface IRefreshHeader {
    void onVisibleHeightChanged(int visibleHeight);

    void onRefresh();

    void onComplete();
}
