package com.lexandera.mosembro.jsinterfaces;

import android.content.Intent;
import android.net.Uri;

import com.lexandera.mosembro.Mosembro;
import com.lexandera.mosembro.R;
import com.lexandera.mosembro.SmartAction;

/** 
 * Creates instances of smart actions which enable user to add events to his google calendar. 
 * It is used by /res/raw/event_to_gcal.js 
 */
public class EventToGcalInterface extends AbstractJSInterface
{
    private Mosembro browser;
    
    public EventToGcalInterface(Mosembro browser)
    {
        this.browser = browser;
    }
    
    public String addEvent(final String eventLocation, final String eventSummary, final String startDate, final String endDate)
    {
        if (eventSummary != null && startDate != null && endDate != null) {
            final String addLink = "http://www.google.com/calendar/event?action=TEMPLATE"
                    + "&text="
                    + Uri.encode(eventSummary)
                    + "&dates="
                    + startDate
                    + "/"
                    + endDate
                    + (eventLocation != null ? "&location="
                            + Uri.encode(eventLocation) : "");
            
            final SmartAction sa = new SmartAction()
            {
                @Override
                public void execute()
                {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(addLink));
                    browser.startActivity(i);
                }
                
                @Override
                public String getLongDescription()
                {
                    return "Add \"" + eventSummary + "\" to my Google calendar";
                }
                
                @Override
                public String getShortDescription()
                {
                    return "Add \"" + eventSummary + "\" to my calendar";
                }
                
                @Override
                public int getIconResourceid()
                {
                    return R.drawable.mf_list_calendar;
                }
            };
            
            int actionId = browser.addSmartAction(sa);
            
            if (browser.getEnableContentRewriting()) {
                return actionLink(actionId, sa.getShortDescription());
            }
        }
        
        return null;
    }
}
