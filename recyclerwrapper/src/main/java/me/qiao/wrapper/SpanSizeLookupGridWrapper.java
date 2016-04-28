package me.qiao.wrapper;

import android.support.v7.widget.GridLayoutManager;

class SpanSizeLookupGridWrapper extends GridLayoutManager.SpanSizeLookup {

    private final GridLayoutManager gridLayoutManager;
    private final GridLayoutManager.SpanSizeLookup wrappedSpanSizeLookup;
    private final RecyclerWrapper.QAdapter wrapperAdapter;

    public SpanSizeLookupGridWrapper(GridLayoutManager gridLayoutManager,
                                     RecyclerWrapper.QAdapter wrapperAdapter) {
        this.gridLayoutManager = gridLayoutManager;
        this.wrappedSpanSizeLookup = gridLayoutManager.getSpanSizeLookup();
        this.wrapperAdapter = wrapperAdapter;
    }

    @Override
    public int getSpanSize(int position) {
        if (wrapperAdapter.isRefreshHeader(position) ||
                wrapperAdapter.isLoadMoreFooter(position)) {
            return gridLayoutManager.getSpanCount();
        } else {
            return wrappedSpanSizeLookup.getSpanSize(position);
        }
    }

    public GridLayoutManager.SpanSizeLookup getWrappedSpanSizeLookup() {
        return wrappedSpanSizeLookup;
    }
}