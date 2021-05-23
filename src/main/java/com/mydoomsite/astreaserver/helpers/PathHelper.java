package com.mydoomsite.astreaserver.helpers;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PathHelper
{
    private static final Pattern InvalidFileNamePattern = Pattern.compile("[\u0000-\u001F\"*/:<>?\\\\|]", Pattern.CASE_INSENSITIVE);
    
    /**
     * Checks whether a given filename is valid or not.
     * @param fileName The filename to check.
     * @return True if the filename is valid, otherwise false.
     */
    public static boolean IsFileNameValid(String fileName)
    {
        Matcher matcher = InvalidFileNamePattern.matcher(fileName);
        return !matcher.find();
    }
    
    /**
     * Checks whether a given path is valid or not.
     * @param fileName The path to check.
     * @return True if the path is valid, otherwise false.
     */
    public static boolean IsPathValid(String path)
    {
        try
        {
            Paths.get(path);
        }
        catch (InvalidPathException ex)
        {
            return false;
        }

        return true;
    }
}
