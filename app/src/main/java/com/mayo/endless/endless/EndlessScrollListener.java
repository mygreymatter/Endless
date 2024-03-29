package com.mayo.endless.endless;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by Mahayogi Lakshmipathi on 8/12/16.
 *
 * @author <a href="mailto:mygreymatter@gmail.com">Mahayogi Lakshmipathi</a>
 * @version 1.0
 */

public class EndlessScrollListener extends RecyclerView.OnScrollListener {

    private static final String LOG = "Endless";
    private int mLoadingThreshold = 8;
    private int mRefreshingThreshold = 3;
    private int mPageSize = 10;
    private int mLastPage = 1;
    private int mDisplayedPagesCount = 0;
    private boolean isLoading;
    private boolean isRefreshing;

    private OnEndlessListener mListener;

    public EndlessScrollListener(OnEndlessListener listener) {
        mListener = listener;
    }

    public EndlessScrollListener(OnEndlessListener listener, int mLoadingThreshold, int mPageSize) {
        this.mListener = listener;
        this.mLoadingThreshold = mLoadingThreshold;
        this.mPageSize = mPageSize;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

        //Log.d(LOG, "Scroll Up: " + dy + " Last: " + lastVisibleItem + " Threshold: " + mLoadingThreshold);

        if (dy > 0 && !isLoading && lastVisibleItem >= mLoadingThreshold) {
            isLoading = true;
            mListener.loadMore();
        } else if (dy < 0 && !isRefreshing &&
                (mLastPage > 3) &&
                (firstVisibleItem <= mRefreshingThreshold)) {
            Log.d(LOG, "Refresh More");
            isRefreshing = true;
            mListener.showToast("Refresh More");
            mListener.refresh();
        }

        if (isLoading) {
            //Log.d(LOG,"Already Loading");
            //mListener.showToast("Already loading");
        }
    }

    public int getNextPage() {
        return mLastPage;
    }

    public void decrementCurrentPage() {
        mLastPage--;
    }

    public int getPageSize() {
        return mPageSize;
    }

    public int getDisplayedPages() {
        return mDisplayedPagesCount;
    }

    public void decrementDisplayedPages() {
        mDisplayedPagesCount--;
    }

    public void incrementDisplayedPages() {
        mDisplayedPagesCount++;
    }

    public void setLoadingThreshold() {
        if(mLastPage <= 2)
            mLoadingThreshold = 8;
        else if( mLastPage > 2)
            mLoadingThreshold = 18;

        /*if (mLastPage == 1 && mLoadingThreshold > mPageSize)
            mLoadingThreshold -= mPageSize;
        else if (mLastPage > 1 && mLoadingThreshold < mPageSize)
            //check if the last page is more than 1
            //then threshold must be greater than 8
            mLoadingThreshold += mPageSize;*/
    }

    public void onLoadFinished() {
        mLastPage++;
        mDisplayedPagesCount++;
        isLoading = false;
        mListener.showToast("Finished Loading");
        setLoadingThreshold();
        Log.d(LOG, "Loading Finished");
    }

    public void onRefreshFinished() {
        mLastPage--;
        mDisplayedPagesCount--;
        isRefreshing = false;
        mListener.showToast("Finished Refreshing");
        setLoadingThreshold();
        Log.d(LOG, "Refresh Finished");

    }

    public interface OnEndlessListener {
        public void refresh();

        public void loadMore();

        public void showToast(String message);
    }


}
