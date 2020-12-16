package com.example.huabei_competition.widget;


import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

/**
 * Create by FanChenYang
 */
public abstract class MyRecyclerAdapter<T> extends RecyclerView.Adapter<MyRecyclerAdapter.MyHolder> {
    private List<T> resources;
    private static final String TAG = "MyRecyclerAdapter";

    public List<T> getResources() {
        return resources;
    }

    public void setResources(List<T> resources) {
        this.resources = resources;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return MyHolder.getInstance(parent, getLayoutId(viewType));
    }

    public abstract int getLayoutId(int viewType);

    public MyRecyclerAdapter(List<T> resources) {
        this.resources = resources;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        bindView(holder, position, resources.get(position));
    }

    public abstract void bindView(MyHolder holder, int position, T t);

    @Override
    public int getItemCount() {
        return resources.size();
    }


    public abstract void addResource(T data);

    public static class MyHolder extends RecyclerView.ViewHolder {
        // 缓存view
        protected SparseArray<View> views;
        protected View mView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            views = new SparseArray<>();
        }

        public static MyHolder getInstance(ViewGroup parent, int layoutId) {
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new MyHolder(view);
        }

        public <T extends View> T getView(int viewId) {
            View view = views.get(viewId);
            if (view == null) {
                view = mView.findViewById(viewId);
            }
            return (T) view;
        }

        public void setText(String text, int id) {
            TextView textView = getView(id);
            textView.setText(text);
        }
    }
}
