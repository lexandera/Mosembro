package com.lexandera.mosembro.util;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import android.content.res.Resources;

/**
 * Utility functions for reading files
 */
public class Reader
{
    /**
     * Reads a file from /raw/res/ and returns it as a String
     * @param res Resources instance for Mosembro
     * @param resourceId ID of resource (ex: R.raw.resource_name)
     */
    public static String readRawString(Resources res, int resourceId)
    {
    	StringBuilder sb = new StringBuilder();
        Scanner s = new Scanner(res.openRawResource(resourceId));

        while (s.hasNextLine()) {
        	sb.append(s.nextLine() + "\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Reads a file from /raw/res/ and returns it as a byte array
     * @param res Resources instance for Mosembro
     * @param resourceId ID of resource (ex: R.raw.resource_name)
     * @return byte[] if successful, null otherwise
     */
    public static byte[] readRawByteArray(Resources res, int resourceId)
    {
        InputStream is = null;
        byte[] raw = new byte[] {};
        try {
            is = res.openRawResource(resourceId);
            raw = new byte[is.available()];
            is.read(raw);
        }
        catch (IOException e) {
            e.printStackTrace();
            raw = null;
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return raw;
    }
    
    /**
     * Reads a remote file and returns it as as a String
     * @param fileUrl URL of remote file
     */
    public static String readRemoteString(String fileUrl)
    {
        StringBuilder sb = new StringBuilder();
        
        try {
            URLConnection connection = (new URL(fileUrl)).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();
            
            Scanner s = new Scanner(connection.getInputStream());
            
            while (s.hasNextLine()) {
                sb.append(s.nextLine() + "\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return sb.toString();
    }
    
    /**
     * Reads a remote file and returns it as as a byte array
     * @param fileUrl URL of remote file
     * @return byte[] if successful, null otherwise
     */
    public static byte[] readRemoteByteArray(String fileUrl)
    {
        InputStream is = null;
        byte[] raw = new byte[] {};
        try {
            URLConnection connection = (new URL(fileUrl)).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();
            is = connection.getInputStream();
            raw = new byte[is.available()];
            is.read(raw);
        }
        catch (Exception e) {
            e.printStackTrace();
            raw = null;
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return raw; 
    }
}



