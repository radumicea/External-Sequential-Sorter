package com.radu.external_sorter.Objects_IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class ObjectReader
{
    private final File file;
    private final FileInputStream fin; 
    private final ObjectInputStream oin;

    public ObjectReader(String path) throws IOException
    {
        this(new File(path));
    }

    public ObjectReader(File file) throws IOException
    {
        this.file = file;
        this.file.createNewFile();

        fin = new FileInputStream(file);
        oin = new ObjectInputStream(fin);
    }

    public Serializable read() throws IOException, ClassNotFoundException
    {
        try
        {
            Serializable obj = (Serializable)oin.readObject();
            return obj;
        }

        catch(ClassNotFoundException e)
        {
            throw new ClassNotFoundException("The class you want to read could not be found in " + file.getPath());
        }
    }

    public void finish() throws IOException
    {
        oin.close();
        fin.close();
    }
}
