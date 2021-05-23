package com.mydoomsite.astreaserver.datatypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.mydoomsite.astreaserver.helpers.PathHelper;

public class SafeFile
{
    private File file;
    private boolean backupOnWrite;
    private String fullPath;
    
    public SafeFile(String path, String fileName) throws IOException
    {
        this(path, fileName, false);
    }
    
    public SafeFile(String path, String fileName, boolean backupOnWrite) throws IOException
    {
        if(!PathHelper.IsFileNameValid(fileName))
            throw new IllegalArgumentException("Invalid characters in file name '" + fileName + "'");
        
        // Validate path
        Paths.get(path);
        
        this.file = new File(path, fileName);
        this.backupOnWrite = backupOnWrite;
        this.fullPath = file.getCanonicalPath();
    }
    
    public File getFile()
    {
        return new File(fullPath);
    }
    
    public String getFullPath()
    {
        return fullPath;
    }
    
    public boolean Exists()
    {
        return file.exists();
    }
    
    public InputStream OpenRead() throws AccessDeniedException, IOException
    {
        if(file.exists())
        {
            if(!file.canRead())
                throw new AccessDeniedException("Access to the path '" + fullPath + "' is denied");
            
            if(file.isDirectory())
                throw new IOException("File '" + fullPath + "' is a directory");
        }
        
        return new FileInputStream(fullPath);
    }
    
    public OutputStream OpenWrite() throws AccessDeniedException, IOException
    {
        return OpenWrite(false);
    }
    
    public OutputStream OpenWrite(boolean append) throws AccessDeniedException, IOException
    {
        if(file.exists())
        {
            if(!file.canWrite())
                throw new AccessDeniedException("Access to the path '" + fullPath + "' is denied");
            
            if(file.isDirectory())
                throw new IOException("File '" + fullPath + "' is a directory");
            
            if(backupOnWrite)
                Files.move(Paths.get(fullPath), Paths.get(fullPath + ".old"), StandardCopyOption.REPLACE_EXISTING);
        }
        
        return new FileOutputStream(fullPath, append);
    }
}
