package com.radu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.radu.external_sorter.Objects_IO.*;
import com.radu.People.*;
import com.radu.external_sorter.ExternalSorter;

public class AppTest 
{ 
    @Test
    public void shouldAnswerWithTrue() throws IOException, ClassNotFoundException
    {
        String path = "People.dat";
        String sortedName = "sorted_" + path;

        ObjectWriter w = new ObjectWriter(path);
        ArrayList<Person> list = new ArrayList<Person>(300000);

        for (int i = 0; i < 300000; i++)
        {
            Person p = new Person();

            w.write(p);
            list.add(p);
        }
        w.finish();

        WeightCmp weightCmp = new WeightCmp();

        ExternalSorter.sort(path, sortedName, 776, weightCmp);

        list.sort(weightCmp);

        ObjectReader r = new ObjectReader(sortedName);

        int i = 0;
        boolean ok = true;

        while (ok)
        {
            try
            {
                Person p = (Person)r.read();
                if (weightCmp.compare(p, list.get(i)) != 0)
                {
                    ok = false;
                }
                i++;
            }

            catch (EOFException eof)
            {
                break;
            }
        }

        r.finish();

        File f = new File(path);
        f.delete();

        f = new File(sortedName);
        f.delete();

        assertTrue(ok == true && i == 300000);
    }
}
