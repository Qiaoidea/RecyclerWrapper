package me.qiao.wrapper;

/**
 * Created by Qiao on 2016/4/23.
 * function：
 */
public interface IRefreshHeader {
    void onVisibleScopeChanged(int visibleHeight);

    boolean isEnughToRefresh();

    void onRefresh();

    void onComplete();
}
