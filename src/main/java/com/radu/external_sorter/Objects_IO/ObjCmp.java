package com.radu.external_sorter.Objects_IO;

import java.io.Serializable;
import java.util.Comparator;

public final class ObjCmp<T extends Serializable> implements Comparator<Serializable>
{
    private final Comparator<T> cmp;

    public ObjCmp(Comparator<T> cmp)
    {
        this.cmp = cmp;
    }

    @SuppressWarnings("unchecked")
    public int compare(Serializable a, Serializable b)
    {
        return cmp.compare((T)a, (T)b);
    }
}