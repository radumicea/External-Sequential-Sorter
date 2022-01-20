package com.radu.external_sorter;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.radu.external_sorter.Objects_IO.*;

@SuppressWarnings("unchecked")
public final class ExternalSorter
{
    private ExternalSorter(){}

    public static<T extends Serializable> void sort
    (
        String inputPath,
        String sortedOutName,
        int maxObjReadInMemory,
        Comparator<T> cmp
    ) throws IOException, ClassNotFoundException
    {
        sort(new File(inputPath), sortedOutName, maxObjReadInMemory, cmp);
    }

    public static<T extends Serializable> void sort
    (
        File file,
        String sortedOutName,
        int maxObjReadInMemory,
        Comparator<T> cmp
    ) throws IOException, ClassNotFoundException
    {
        short nrFiles = createHelperFiles(file, maxObjReadInMemory, cmp);

        if (nrFiles > maxObjReadInMemory / 2)
        {
            deleteFiles(nrFiles);
            throw new OutOfMemoryError("maxObjReadInMemory must be > ceil(sqrt(2 * totalObjCountInFile))");
        }

        ObjectReader[] r = initObjectReaders(nrFiles);
        PriorityQueue<IndexValuePair<T>> queue = initQueue(r, nrFiles, maxObjReadInMemory / 2, cmp);
        mergeAndWrite(queue, r, nrFiles, file, sortedOutName, maxObjReadInMemory);
        finishObjectReaders(r, nrFiles);
    }

    private static<T extends Serializable> short createHelperFiles
        (File file, int maxCount, Comparator<T> cmp)
            throws IOException, ClassNotFoundException
    {
        ObjectReader r = new ObjectReader(file);
        ObjCmp<T> objCmp = new ObjCmp<T>(cmp);
        Serializable[] a = new Serializable[maxCount];
        ArrayList<Integer> gap = initGap(maxCount);

        short fileNr = 0;
        boolean repeat = true;

        while (repeat)
        {
            int i;
            for (i = 0; i < maxCount; i++)
            {
                try
                {
                    a[i] = r.read();
                }
                catch (EOFException e)
                {
                    repeat = false;
                    break;
                }
            }

            if (i > 0)
            {
                ObjectWriter w = new ObjectWriter("e_sort_" + Short.toString(fileNr) + ".tmp");

                shellSort(a, i, gap, objCmp);

                for (int j = 0; j < i; j++)
                {
                    w.write(a[j]);
                }

                w.finish();
                fileNr++;
            }
        }

        r.finish();
        return fileNr;
    }

    // Sedgewick's gap sequence
    private static ArrayList<Integer> initGap(int n)
    {
        ArrayList<Integer> gap = new ArrayList<Integer>(64);

        int i = -1;
        int g = 1;

        do
        {
            i++;

            if (i % 2 == 0)
            {
                g = 9 * (twoAt(i) - twoAt(i / 2)) + 1;
                gap.add(g);
            }

            else
            {
                g = 8 * twoAt(i) - 6 * twoAt((i + 1) / 2) + 1;
                gap.add(g);
            }

        } while (g <= n / 3);

        return gap;
    }

    private static int twoAt(int i)
    {
        return (1 << i);
    }

    private static<T extends Serializable> void shellSort
        (Serializable[] a, int n, ArrayList<Integer> gap, ObjCmp<T> objCmp)
    {
        int i = gap.size() - 1;
        int l;
        int r;
        Serializable key;

        while (i >= 0)
        {
            int g = gap.get(i);

            for (r = g; r < n; r++)
            {
                key = a[r];
                l = r - g;

                while (l >= 0 && objCmp.compare(key, a[l]) < 0)
                {
                    a[l + g] = a[l];
                    l -= g;
                }
                a[l + g] = key;
            }

            i--;
        }
    }

    private static ObjectReader[] initObjectReaders(short n) throws IOException
    {
        ObjectReader[] r = new ObjectReader[n];
        for (short i = 0; i < n; i++)
        {
            r[i] = new ObjectReader("e_sort_" + Short.toString(i) + ".tmp");
        }

        return r;
    }

    private static<T extends Serializable> PriorityQueue<IndexValuePair<T>> initQueue
        (ObjectReader[] r, short n, int size, Comparator<T> cmp)
            throws ClassNotFoundException, IOException
    {
        IVPComparator<T> ivpCmp = new IVPComparator<T>(cmp);
        PriorityQueue<IndexValuePair<T>> pq = new PriorityQueue<IndexValuePair<T>>(size, ivpCmp);

        for (short i = 0; i < n; i++)
        {
            for (int j = 0; j < size / n; j++)
            {
                try
                {
                    IndexValuePair<T> ivp = new IndexValuePair<T>(i, (T)r[i].read());
                    pq.add(ivp);
                }
                catch (EOFException e)
                {
                    break;
                }
            }
        }

        return pq;
    }

    private static<T extends Serializable> void mergeAndWrite
    (
        PriorityQueue<IndexValuePair<T>> queue,
        ObjectReader[] r,
        short n,
        File file,
        String sortedName,
        int maxCount
    )
    throws IOException, ClassNotFoundException
    {
        ObjectWriter w = new ObjectWriter(sortedName);
        Serializable[] out = new Serializable[maxCount / 2];

        int i = 0;
        while (!(queue.isEmpty()))
        {
            IndexValuePair<T> smallest = queue.poll();
            out[i] = smallest.value;
            i++;

            try
            {
                smallest.value = (T)r[smallest.index].read();
                queue.add(smallest);
            }
            catch (EOFException e)
            {
            }

            if (i == maxCount / 2)
            {
                for (int j = 0; j < i; j++)
                {
                    w.write(out[j]);
                }

                i = 0;
            }
        }

        if (i != 0)
        {
            for (int j = 0; j < i; j++)
            {
                w.write(out[j]);
            }
        }

        w.finish();

        deleteFiles(n);
    }

    private static void deleteFiles(short n)
    {
        for (short i = 0; i < n; i++)
        {
            File f = new File("e_sort_" + Short.toString(i) + ".tmp");
            f.delete();
        }
    }

    private static void finishObjectReaders(ObjectReader[] r, short n) throws IOException
    {
        for (short i = 0; i < n; i++)
        {
            r[i].finish();
        }
    }
}

final class IVPComparator<T extends Serializable> implements Comparator<IndexValuePair<T>>
{
    final Comparator<T> cmp;

    IVPComparator(Comparator<T> cmp)
    {
        this.cmp = cmp;
    }

    public int compare(IndexValuePair<T> a, IndexValuePair<T> b)
    {
        return cmp.compare(a.value, b.value);
    }
}

final class IndexValuePair<T extends Serializable>
{
    final short index;
    T value;

    IndexValuePair(short index, T value)
    {
        this.index = index;
        this.value = value;
    }
}