package com.lexandera.mosembro.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.res.Resources;

public class MosembroUtil
{
    public static String readRawString(Resources res, int resourceId)
    {
        InputStream is = res.openRawResource(resourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
        StringBuilder sb = new StringBuilder();
        String line = null;
        
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return sb.toString();
    }
    
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
    
}
