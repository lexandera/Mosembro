package com.lexandera.mosembro.jsinterfaces;

import android.content.Intent;
import android.net.Uri;

import com.lexandera.mosembro.Mosembro;
import com.lexandera.mosembro.R;
import com.lexandera.mosembro.SmartAction;

import org.json.JSONArray;

/** 
 * This JS interface handles window.ActionInterface.execute(id) calls which are
 * triggered by onclick events, attached to "smart links" (when smart links are enabled)
 */
public class ActionInterface
{
    Mosembro browser;
    
    public ActionInterface(Mosembro browser)
    {
        this.browser = browser;
    }
    
    public void executeAction(int id)
    {
        browser.getSmartActions().get(id).execute();
    }
    
    public String getScriptsFor(String category)
    {
        JSONArray jsa = new JSONArray();
        
        if (category.equals("adr")) {
            jsa.put(browser.getScript(R.raw.address_to_gmap));
            jsa.put(browser.getScript(R.raw.adr_journeyplanner));
        }
        else if (category.equals("vevent")) {
            jsa.put(browser.getScript(R.raw.event_to_gcal));
        }
        
        return jsa.toString();
    }
    
    public String addAction(final String action, final String uri, final String icon,
                            final String descShort, final String descLong)
    {
        final SmartAction sa = new SmartAction()
        {
            @Override
            public void execute()
            {
                String intentAction = null;
                try {
                    intentAction = (String)Intent.class.getField(action).get(null);
                }
                catch (Exception e) {}
                
                Intent i = new Intent(intentAction, Uri.parse(uri));
                browser.startActivity(i);
            }
            
            @Override
            public String getLongDescription()
            {
                return descLong;
            }
            
            @Override
            public String getShortDescription()
            {
                return descShort;
            }
            
            @Override
            public int getIconResourceid()
            {
                // TODO: better icon support!
                if ("calendar".equals(icon)) {
                    return R.drawable.mf_list_calendar;
                }
                else if ("map".equals(icon)) {
                    return R.drawable.mf_list_map;
                }
                else if ("journeyplanner".equals(icon)) {
                    return R.drawable.mf_list_journeyplanner;
                }
                
                return 0;
            }
        };
        
        int actionId = browser.addSmartAction(sa);
        browser.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                browser.updateTitleIcons();
            }});
        
        if (browser.getEnableContentRewriting()) {
            return actionLink(actionId, sa.getShortDescription());
        };
        
        return null;
    }
    
    public String actionLink(int actionId, String text)
    {
        return "<div style=\"display: block; clear: both; margin: 5px 5px 5px 2px;\">"+
            "<a href=\"/null\" " +
            "onclick=\"window.ActionInterface.executeAction("+Integer.toString(actionId)+"); " +
            "return false;\">" + text + 
            "</a>" +
            "</div>";
    }
}
