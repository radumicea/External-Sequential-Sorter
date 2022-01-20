package com.radu.People;

import java.io.Serializable;
import java.text.DecimalFormat;

public class Person implements Serializable
{
    static final long serialVersionUID = 100L;

    private static final DecimalFormat df = new DecimalFormat("0.00");
    private final String name;
    private byte age;
    private float height;
    private float weight;

    public Person()
    {
        name = generateString();
        age = generateByte(20, 100);
        height = generateFloat(1.4f, 2.1f);
        weight = generateFloat(35, 120);
    }

    public String getName()
    {
        return name;
    }

    public byte getAge()
    {
        return age;
    }

    public float getHeight()
    {
        return height;
    }

    public float getWeight()
    {
        return weight;
    }

    private String generateString()
    {
        int size = (int)((Math.random() * (11 - 1)) + 1);
        
        String letters = "abcdefghijklmnopqrstuvxyz";
        StringBuilder name = new StringBuilder(size);

        for (int i = 0; i < size; i++)
        {
            char c = letters.charAt((int)(Math.random() * 25));

            if (i == 0)
            {
                c = Character.toUpperCase(c);
            }

            name.append(c);
        }

        return name.toString();
    }

    private byte generateByte(int min, int max)
    {
        int age = (int)((Math.random() * (max - min)) + min);
        age &= 0x7F;

        return (byte)age;
    }

    private float generateFloat(float min, float max)
    {
        return min + (float)Math.random() * (max - min);
    }

    public String toString()
    {
        return name + " ".repeat(10 - name.length()) + " " + age + " "
               + df.format(height) + " " + df.format(weight) + "\n";
    }
}