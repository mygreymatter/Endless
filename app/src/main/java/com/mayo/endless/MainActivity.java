package com.mayo.endless;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.mayo.endless.endless.EndlessScrollListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements EndlessScrollListener.OnEndlessListener {

    private static final String LOG = "Endless";

    RecyclerView recyclerView;
    MyAdapter mAdapter;
    int index = 0;

    ArrayList<String> nNames = new ArrayList<>();
    ArrayList<String> nPrevPageNames = new ArrayList<>();
    private EndlessScrollListener endlessListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new MyAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        endlessListener = new EndlessScrollListener(this);
        mAdapter.setNames(nNames);
        recyclerView.addOnScrollListener(endlessListener);

        loadMore();
    }

    @Override
    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refresh(){
        int pageSize = endlessListener.getPageSize();

        for(int i = 0; i < pageSize ; i++){
            nNames.add(i,nPrevPageNames.get(i));
        }

        nPrevPageNames.subList(0,pageSize).clear();

        endlessListener.incrementDisplayedPages();
        if(endlessListener.getDisplayedPages() > 2){
            nNames.subList(2 * pageSize,nNames.size()).clear();
            mAdapter.remove(2 * pageSize,3 * pageSize);

            endlessListener.onRefreshFinished();
        }

    }

    @Override
    public void loadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final int lastPage = endlessListener.getLastPage();
                int pageSize = endlessListener.getPageSize();
                int size = nNames.size();

                for (index = size; index < (pageSize * (lastPage + 1)); index++) {
                    Log.d(LOG, "Index : " + index);
                    nNames.add("Yaagi " + String.valueOf(index));
                }
                //just for tracking
                index--;

                endlessListener.onLoadFinished();
                Log.d(LOG, "Names Size : " + nNames.size());
                mAdapter.add(size);

                //display only two pages at a time
                if (endlessListener.getDisplayedPages() > 2) {
                    size = endlessListener.getPageSize();
                    nPrevPageNames.clear();

                    for (int index = 0; index < size; index++) {
                        nPrevPageNames.add(nNames.get(index));
                        Log.d(LOG, "Removed: " + nNames.get(index) + " " + index);
                    }

                    //removes range of items at one shot
                    nNames.subList(0, 10).clear();
                    //endlessListener.decrementCurrentPage();
                    endlessListener.decrementDisplayedPages();
                    mAdapter.remove(0, 10);
                }
            }
        }, 2000);
    }
}
