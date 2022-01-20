package com.radu.external_sorter.People;

import java.util.Comparator;

public class HeightCmp implements Comparator<Person>
{
    public int compare(Person a, Person b)
    {
        return Float.compare(a.getHeight(), b.getHeight());
    }
}