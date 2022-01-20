package com.radu.external_sorter.Objects_IO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectWriter
{
    private final FileOutputStream fout; 
    private final ObjectOutputStream oout;

    public ObjectWriter(String path) throws IOException
    {
        this(new File(path));
    }

    public ObjectWriter(File file) throws IOException
    {
        file.createNewFile();

        fout = new FileOutputStream(file);
        oout = new ObjectOutputStream(fout);
    }

    public void write(Serializable objToWrite) throws IOException
    {
        oout.writeObject(objToWrite);
    }

    public void finish() throws IOException
    {
        oout.flush();
        oout.close();
        fout.close();
    }
}
