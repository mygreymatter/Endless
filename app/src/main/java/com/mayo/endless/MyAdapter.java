package com.mayo.endless;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mahayogi Lakshmipathi on 7/12/16.
 *
 * @author <a href="mailto:mygreymatter@gmail.com">Mahayogi Lakshmipathi</a>
 * @version 1.0
 */

class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG = "Endless";
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private LayoutInflater mInflater;
    private ArrayList<String> nNames;

     void setNames(ArrayList<String> names) {
        nNames = names;
        notifyDataSetChanged();
    }

    void add(int pos){
        Log.d(LOG,"RangeInserted: " + pos);
        notifyItemRangeInserted(pos,10);
    }

    void remove(int index,int count) {
        notifyItemRangeRemoved(index,count);
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = nNames.size() == position ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        //Log.d(LOG,"GetItemType position: " + position + " ViewType: " +  viewType);
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(parent.getContext());
        }
        //Log.d(LOG,"onCreateViewHolder ViewType: " +  viewType);
        if(viewType == VIEW_TYPE_ITEM) {
            return new ItemViewWrapper(mInflater.inflate(R.layout.list_item, parent, false));
        }else {
            return new LoadingViewWrapper(mInflater.inflate(R.layout.list_item_loading, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Log.d(LOG,"onBind Position: " + position +" ViewType: " + (nNames.size() + 1 == position ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM));
        if(holder instanceof ItemViewWrapper) {
            ItemViewWrapper itemViewWrapper = (ItemViewWrapper) holder;
            itemViewWrapper.nameText.setText(nNames.get(position));
        }else if(holder instanceof LoadingViewWrapper){
            LoadingViewWrapper loadingViewWrapper = (LoadingViewWrapper) holder;

        }
    }

    @Override
    public int getItemCount() {
        return (nNames == null || nNames.size() == 0) ? 0 : nNames.size() + 1;
    }

    private static class ItemViewWrapper extends RecyclerView.ViewHolder {
        TextView nameText;

        ItemViewWrapper(View itemView) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.name);
        }
    }

    private static class LoadingViewWrapper extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewWrapper(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }
}
