package com.iamandroidgeek.dexter.SwipeRefresh_JSON_Volley.helper;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iamandroidgeek.dexter.SwipeRefresh_JSON_Volley.Activity.MainActivity;
import com.iamandroidgeek.dexter.SwipeRefresh_JSON_Volley.R;
import com.iamandroidgeek.dexter.SwipeRefresh_JSON_Volley.model.Fish;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dexter on 3/10/2018.
 */

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public String TAG = "handle";

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private LayoutInflater inflater;
    private ArrayList<Fish> fishList;

    private OnLoadMoreListener onLoadMoreListener;
    private LinearLayoutManager mLinearLayoutManager;

    private boolean isMoreLoading = false;
    private int visibleThreshold = 1;
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private Activity activity;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public MyAdapter(MainActivity onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        fishList = new ArrayList<>();
        this.activity = onLoadMoreListener;
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

//    public MyAdapter(Activity activity, ArrayList<Fish> fishList) {
//        this.inflater = LayoutInflater.from(activity);
//        this.fishList = fishList;
//        this.activity = activity;
//    }

    public void setRecyclerView(RecyclerView mView) {
        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (!isMoreLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isMoreLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return fishList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar, parent, false));
        }
    }

    public void addAll(ArrayList<Fish> lst) {
        fishList.clear();
        fishList.addAll(lst);
        notifyDataSetChanged();
    }

    public void addItemMore(List<Fish> lst) {
        fishList.addAll(lst);
        notifyItemRangeChanged(0, fishList.size());
    }


//    boolean setNotifyOnChange (boolean notifyOnChange){ return notifyOnChange; }
//
//    public Object getItem(int position) { return fishList.get(position); }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            Fish singleFish = fishList.get(position);
            ((MyHolder)holder).tvName.setText(singleFish.getName());
            Glide
                    .with(activity)
                    .load(fishList.get(position).getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .crossFade()
                    .into(((MyHolder)holder).ivImage);

        }
    }

    public void setMoreLoading(boolean isMoreLoading) {
        this.isMoreLoading = isMoreLoading;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return fishList.size();
    }

    public void setProgressMore(final boolean isProgress) {
        if (isProgress) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    fishList.add(null);
                    notifyItemInserted(fishList.size() - 1);
                }
            });
        } else {
            fishList.remove(fishList.size() - 1);
            notifyItemRemoved(fishList.size());
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvName;

        MyHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.imageView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pBar;

        public ProgressViewHolder(View v) {
            super(v);
            pBar = v.findViewById(R.id.pBar);
        }
    }


}
