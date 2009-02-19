package com.lexandera.mosembro;


import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lexandera.mosembro.util.Reader;

public class ActionStore extends SQLiteOpenHelper
{
    private static final String TYPE_MICROFORMAT = "microformat";
    private static final int DB_VERSION = 5;
    
    private Mosembro browser;
    private Bitmap defaultActionBitmap;
    private HashMap<String, Bitmap> iconCache;
    private HashMap<String, String[]> scriptCache;
    
    public ActionStore(Mosembro context)
    {
        super(context, "mosembro", null, DB_VERSION);
        this.browser = context;
        
        clearCache();
        
        /* load default icon for actions */
        byte[] defaultBytes = Reader.readRawByteArray(browser.getResources(), R.raw.mf_list_no_icon);
        defaultActionBitmap = BitmapFactory.decodeByteArray(defaultBytes, 0, defaultBytes.length);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(Reader.readRawString(browser.getResources(), R.raw.db_create));
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE actions ADD type TEXT;");
        }
    }
    
    void clearCache()
    {
        iconCache = new HashMap<String, Bitmap>();
        scriptCache = new HashMap<String, String[]>();
    }
    
    /* (re)installs built-in actions */
    public void updateBuiltInActions()
    {
        Resources res = browser.getResources();
        
        installAction("com.lexandera.scripts.AddressToGMap", 
                "Show address on map",
                TYPE_MICROFORMAT, "adr", 
                Reader.readRawString(res, R.raw.adr_to_gmap),
                Reader.readRawByteArray(res, R.raw.mf_list_map));
        
        installAction("com.lexandera.scripts.LondonJourneyPlanner", 
                "London journey planner",
                TYPE_MICROFORMAT, "adr", 
                Reader.readRawString(res, R.raw.adr_journeyplanner),
                Reader.readRawByteArray(res, R.raw.mf_list_journeyplanner));
        
        installAction("com.lexandera.scripts.BayAreaTripPlanner", 
                "Bay area trip planner",
                TYPE_MICROFORMAT, "adr", 
                Reader.readRawString(res, R.raw.adr_bayarea_tripplanner),
                Reader.readRawByteArray(res, R.raw.mf_list_bayarea_tripplanner));
        
        installAction("com.lexandera.scripts.AddressCopyToClipboard",
                "Copy address to clipboard",
                TYPE_MICROFORMAT, "adr", 
                Reader.readRawString(res, R.raw.adr_copy),
                Reader.readRawByteArray(res, R.raw.mf_list_copy));
        
        installAction("com.lexandera.scripts.EventToGCal", 
                "Add event to Google calendar",
                TYPE_MICROFORMAT, "vevent", 
                Reader.readRawString(res, R.raw.event_to_gcal),
                Reader.readRawByteArray(res, R.raw.mf_list_calendar));
    }
    
    public void installAction(String actionId, String name, String type, String handles, String script, String iconURL)
    {
        byte[] icon = new byte[] {};
        installAction(actionId, name, type, handles, script, icon);
    }
    
    public void installAction(String actionId, String name, String type, String handles, String script, byte[] icon)
    {
        ContentValues vals = new ContentValues();
        vals.put("action_id", actionId);
        vals.put("name", name);
        vals.put("type", type);
        vals.put("handles", handles);
        vals.put("script", script);
        vals.put("icon", icon);
        
        deleteAction(actionId);
        getWritableDatabase().insert("actions", null, vals);
        
        clearCache();
    }
    
    public void deleteAction(String actionId)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM actions WHERE action_id = ?;", new String[] { actionId });
        
        clearCache();
    }
    
    public String[] getStriptsForMicroformatActions(String microformat)
    {
        if (!scriptCache.containsKey(microformat)) {
            Cursor data = getReadableDatabase().rawQuery(
                    "SELECT script " +
                    "FROM actions " +
                    "WHERE type = ? " +
                    "AND handles = ?", 
                    new String[] {TYPE_MICROFORMAT, microformat });
            
            String[] scripts = new String[data.getCount()];
            int i = 0;
            while (data.moveToNext()) {
                scripts[i] = data.getString(0);
                ++i;
            }
            data.close();
            scriptCache.put(microformat, scripts);
        }
        
        return scriptCache.get(microformat);
    }
    
    public Bitmap getIconForAction(String actionId)
    {
        if (!iconCache.containsKey(actionId)) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor data = db.rawQuery("SELECT icon FROM actions WHERE action_id = ?", new String[] { actionId });
            Bitmap bm = null;
            
            if (data.moveToFirst()) {
                byte[] bytes = data.getBlob(0);
                bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
            data.close();
            
            iconCache.put(actionId, bm);
        }
        
        if (iconCache.get(actionId) == null) {
            return defaultActionBitmap;
        }
        
        return iconCache.get(actionId);
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
    
    public boolean installFromUrl(final String url)
    {
        Resources res = browser.getResources();
        String script = Reader.readRemoteString(res, url);
        HashMap<String, String> vals = ActionStore.parseActionScript(script);
        
        if (vals != null) {
            installAction(vals.get("id"), 
                          vals.get("name"), 
                          "microformat", 
                          vals.get("handles"), 
                          script, 
                          Reader.readRemoteByteArray(res, vals.get("icon")));
            
            return true;
        }
        
        return false;
    }
}
