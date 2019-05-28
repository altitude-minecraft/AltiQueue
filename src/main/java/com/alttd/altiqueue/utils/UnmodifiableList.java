package com.alttd.altiqueue.utils;

import java.util.AbstractList;
import java.util.List;

public class UnmodifiableList<T> extends AbstractList<T>
{
    private final List<T>[] lists;

    private int size;

    @SafeVarargs
    public UnmodifiableList(List<T>... lists)
    {
        this.lists = lists;

        for (List<T> list : lists)
        {
            size += list.size();
        }
    }

    @Override
    public T get(int index)
    {
        for (List<T> list : lists)
        {
            if (list.size() < index)
            {
                index -= list.size();
            }
            else
            {
                return list.get(index);
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int size()
    {
        return size;
    }
}
