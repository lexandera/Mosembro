package com.lexandera.mosembro;


import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lexandera.mosembro.util.MosembroUtil;

public class ActionStore extends SQLiteOpenHelper
{
    private static final String TYPE_MICROFORMAT = "microformat";

    private static final int DB_VERSION = 2;
    
    private Mosembro browser;
    
    public ActionStore(Mosembro context)
    {
        super(context, "mosembro", null, DB_VERSION);
        this.browser = context;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(MosembroUtil.readRawString(browser.getResources(), R.raw.db_create));
        updateBuiltInActions();
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE actions ADD type TEXT;");
        }
    }
    
    /* (re)installs built-in actions */
    public void updateBuiltInActions()
    {
        Resources res = browser.getResources();
        installAction("com.lexandera.scripts.adr_to_gmap", TYPE_MICROFORMAT, "adr", 
                MosembroUtil.readRawString(res, R.raw.adr_to_gmap),
                MosembroUtil.readRawByteArray(res, R.raw.mf_list_map));
        installAction("com.lexandera.scripts.adr_journeyplanner", TYPE_MICROFORMAT, "adr", 
                MosembroUtil.readRawString(res, R.raw.adr_journeyplanner),
                MosembroUtil.readRawByteArray(res, R.raw.mf_list_journeyplanner));
        installAction("com.lexandera.scripts.adr_bayarea_tripplanner", TYPE_MICROFORMAT, "adr", 
                MosembroUtil.readRawString(res, R.raw.adr_bayarea_tripplanner),
                MosembroUtil.readRawByteArray(res, R.raw.mf_list_bayarea_tripplanner));
        installAction("com.lexandera.scripts.adr_copy", TYPE_MICROFORMAT, "adr", 
                MosembroUtil.readRawString(res, R.raw.adr_copy),
                MosembroUtil.readRawByteArray(res, R.raw.mf_list_copy));
        
        installAction("com.lexandera.scripts.event_to_gcal", TYPE_MICROFORMAT, "vevent", 
                MosembroUtil.readRawString(res, R.raw.event_to_gcal),
                MosembroUtil.readRawByteArray(res, R.raw.mf_list_calendar));
    }
    
    public void installAction(String action_id, String type, String handles, String script, String iconURL)
    {
        byte[] icon = new byte[] {};
        installAction(action_id, type, handles, script, icon);
    }
    
    public void installAction(String action_id, String type, String handles, String script, byte[] icon)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM actions WHERE action_id = ?;", new String[] { action_id });
        
        ContentValues vals = new ContentValues();
        vals.put("action_id", action_id);
        vals.put("type", type);
        vals.put("handles", handles);
        vals.put("script", script);
        vals.put("icon", icon);
        db.insert("actions", null, vals);
    }
    
    public String[] getActionsForMicroformat(String microformat)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor data = db.rawQuery("SELECT script " +
        		                  "FROM actions " +
        		                  "WHERE type = ? " +
        		                  "AND handles = ?", new String[] {TYPE_MICROFORMAT, microformat });
        
        String[] out = new String[data.getCount()];
        int i = 0;
        while (data.moveToNext()) {
            out[i] = data.getString(0);
            ++i;
        }
        data.close();
        
        return out;
    }
}
