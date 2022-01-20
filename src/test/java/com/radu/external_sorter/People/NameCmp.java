package com.radu.external_sorter.People;

import java.util.Comparator;

public class NameCmp implements Comparator<Person>
{
    public int compare(Person a, Person b)
    {
        return a.getName().compareTo(b.getName());
    }
}