package com.example.huabei_competition.widget;


import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huabei_competition.databinding.ItemProductBinding;

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
        private ViewDataBinding binding;

        public MyHolder(@NonNull ViewDataBinding binding) {
            super(binding.getRoot());
            mView = itemView;
            views = new SparseArray<>();
            this.binding = binding;
        }

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            this.mView = mView;
            views = new SparseArray<>();
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

        public static MyHolder getInstance(ViewGroup parent, int layoutId) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, parent, false);
            return new MyHolder(binding);
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

    /**
     * @param adapter 需要刷新的adapter
     * @param oldList 旧的列表内容
     * @param newList 新的列表内容
     * @param <P>     实现了可比较接口的泛型
     */
    public static <P extends DiffDataCallback.Differ<P>> void diff(MyRecyclerAdapter<P> adapter, List<P> oldList, List<P> newList) {
        DiffDataCallback<P> callback = new DiffDataCallback<>(oldList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setResources(newList);
        result.dispatchUpdatesTo(adapter);
    }
}
