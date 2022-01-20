package com.radu.People;

import java.util.Comparator;

public class AgeCmp implements Comparator<Person>
{
    public int compare(Person a, Person b)
    {
        return a.getAge() - b.getAge();
    }
}