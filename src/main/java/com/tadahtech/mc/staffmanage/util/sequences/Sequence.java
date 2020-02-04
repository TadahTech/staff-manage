package com.tadahtech.mc.staffmanage.util.sequences;

public interface Sequence<T> {
    T get();

    T next();

    T getInterval();

    void add(T object);

    T getStop();

    T getStart();

    void reset();

    void onIteration(Runnable runnable);
}
