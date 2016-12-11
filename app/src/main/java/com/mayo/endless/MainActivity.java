package com.mayo.endless;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.mayo.endless.endless.EndlessScrollListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements EndlessScrollListener.OnEndlessListener {

    private static final String LOG = "Endless";

    RecyclerView recyclerView;
    MyAdapter mAdapter;
    int index = 0;

    ArrayList<Test> nNames = new ArrayList<>();
    //    ArrayList<String> nPrevPageNames = new ArrayList<>();
    private EndlessScrollListener endlessListener;
    private TestAPI testAPI;

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://demo7398861.mockable.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        testAPI = retrofit.create(TestAPI.class);

        loadMore();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refresh() {
        Log.d(LOG, "Refresh");
        getTestList(false);
    }

    @Override
    public void loadMore() {
        getTestList(true);
    }

    private void getTestList(final boolean isloading) {
        int nextPage = endlessListener.getNextPage();
        String page;

        if (isloading) {
            page = String.valueOf(nextPage);
        } else {
            page = String.valueOf(nextPage - 2/*pages displayed*/ - 1);
        }

        Call<ArrayList<Test>> tests = testAPI.getTests(page);

        tests.enqueue(new Callback<ArrayList<Test>>() {
            @Override
            public void onResponse(Call<ArrayList<Test>> call, Response<ArrayList<Test>> response) {
                //Log.d(LOG, "Response: " + response);
                if (response.body() == null)
                    return;

                ArrayList<Test> tester = response.body();
                Log.d(LOG, "Pager: ");
                Log.d(LOG, isloading ? "Loading" : "Refreshing");
                for (Test t : tester)
                    Log.d(LOG, "Name: " + t.name);

                int pageSize = endlessListener.getPageSize();
                int startIndex;

                if (isloading) {
                    startIndex = nNames.size();
                    nNames.addAll(tester);
                    endlessListener.onLoadFinished();
                    mAdapter.add(startIndex);

                    if (endlessListener.getDisplayedPages() > 2) {
                        nNames.subList(0, pageSize).clear();
                        endlessListener.decrementDisplayedPages();
                        mAdapter.remove(0, 10);
                    }

                } else {
                    nNames.addAll(0, tester);
                    endlessListener.incrementDisplayedPages();
                    mAdapter.add(0);

                    int size = nNames.size();
                    startIndex = size - pageSize;
                    nNames.subList(startIndex, startIndex + pageSize).clear();
                    mAdapter.remove(startIndex, 10);

                    endlessListener.onRefreshFinished();
                    //mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Test>> call, Throwable t) {
                Log.d(LOG, "Failed");
            }
        });
    }
}
