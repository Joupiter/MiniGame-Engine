package fr.joupi.api;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

public class MergedList<T> extends AbstractList<T> {

    private final List<T>[] lists;
    private final int size;

    @SafeVarargs
    public MergedList(List<T>... lists) {
        this.lists = lists.clone();
        this.size = Arrays.stream(lists).mapToInt(List::size).sum();
    }

    @Override
    public T get(int index) {
        for (List<T> list : lists)
            if (index < list.size())
                return list.get(index);
            else
                index -= list.size();
        throw new IndexOutOfBoundsException("index");
    }

    @Override
    public int size() {
        return size;
    }

}