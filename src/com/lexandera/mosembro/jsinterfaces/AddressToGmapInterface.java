package com.lexandera.mosembro.jsinterfaces;

import android.content.Intent;
import android.net.Uri;

import com.lexandera.mosembro.Mosembro;
import com.lexandera.mosembro.R;
import com.lexandera.mosembro.SmartAction;

/** 
 * Creates instances of smart actions which display an address using the maps app. 
 * It is used by /res/raw/address_to_gmap.js 
 */
public class AddressToGmapInterface extends AbstractJSInterface
{
    private Mosembro browser;
    
    public AddressToGmapInterface(Mosembro browser)
    {
        this.browser = browser;
    }
    
    public String addAddress(final String street, String locality, String postalCode)
    {
        if (street != null && (locality != null || postalCode != null)) {
            final String fullAddr = street
                    + (locality != null ? ", " + locality : "")
                    + (postalCode != null ? ", " + postalCode : "");
            
            final SmartAction sa = new SmartAction()
            {
                public void execute()
                {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="
                            + Uri.encode(fullAddr)));
                    browser.startActivity(i);
                }
                
                public String getLongDescription()
                {
                    return "Show \"" + street + "\" using maps application";
                }
                
                public String getShortDescription()
                {
                    return "Show \"" + street + "\" on map";
                }
                
                @Override
                public int getIconResourceid()
                {
                    return R.drawable.mf_list_map;
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
