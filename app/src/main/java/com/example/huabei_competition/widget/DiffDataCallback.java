package com.example.huabei_competition.widget;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
/**
 *      Create by FanChenYang at 2020/12/29
 *
 */
public class DiffDataCallback<T extends DiffDataCallback.Differ<T>> extends DiffUtil.Callback {
    private final List<T> oldData;
    private final List<T> newData;

    public DiffDataCallback(List<T> oldData, List<T> newData) {
        this.oldData = oldData;
        this.newData = newData;
    }

    @Override
    public int getOldListSize() {
        return oldData.size();
    }

    @Override
    public int getNewListSize() {
        return newData.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T old = oldData.get(oldItemPosition);
        T fresh = newData.get(newItemPosition);
        return old.isSame(fresh);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T old = oldData.get(oldItemPosition);
        T fresh = newData.get(newItemPosition);
        return old.isContentSame(fresh);
    }

    public interface Differ<T> {
        // 是否为统一个体
        boolean isSame(T t);

        // 同一个体，内容是否相同
        boolean isContentSame(T t);
    }
}
