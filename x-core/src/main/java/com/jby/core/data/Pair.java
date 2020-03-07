package com.jby.core.data;

import lombok.Data;

@Data
public class Pair<T1, T2> {
    public T1 first;
    public T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public static<T1, T2> Pair<T1, T2> of (T1 first, T2 second) {
        return new Pair<>(first, second);
    }
}
