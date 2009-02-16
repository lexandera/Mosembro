package com.lexandera.mosembro.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    
    public static HashMap<String, String> parseActionScript(String text)
    {
        HashMap<String, String> out = new HashMap<String, String>();
        Pattern headerExtractRegex = Pattern.compile("==Action==(.*?)==/Action==", Pattern.DOTALL);
        Pattern fieldExtractRegex = Pattern.compile("^.*?@([^ ]+)\\s*(.*?)\\s*$", Pattern.MULTILINE);
        Matcher headerMatcher = headerExtractRegex.matcher(text);
        
        if (headerMatcher.find()) {
            String header = headerMatcher.group(1);
            
            Matcher fieldMatcher = fieldExtractRegex.matcher(header);
            while (fieldMatcher.find()) {
                String propname = fieldMatcher.group(1);
                String propvalue = fieldMatcher.group(2);
                
                if (!"".equals(propname) && !"".equals(propvalue)) {
                    out.put(propname, propvalue);
                }
            }
        }
        
        if (out.containsKey("name") && out.containsKey("id") && out.containsKey("type") && out.containsKey("handles")) {
            return out;
        }
        
        return null;
    }
}












