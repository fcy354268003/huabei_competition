package com.example.huabei_competition.widget;

import com.contrarywind.adapter.WheelAdapter;

import java.util.List;

public class ArrayWheelAdapter<T> implements WheelAdapter<T> {
    private final List<T> source;

    public ArrayWheelAdapter(List<T> a) {
        source = a;
    }

    @Override
    public int getItemsCount() {
        return source.size();
    }

    @Override
    public T getItem(int index) {
        return source.get(index);
    }

    @Override
    public int indexOf(T o) {
        return source.indexOf(o);
    }
}
