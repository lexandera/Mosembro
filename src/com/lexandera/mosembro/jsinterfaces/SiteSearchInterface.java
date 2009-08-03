package com.lexandera.mosembro.jsinterfaces;

import java.util.HashMap;

import com.lexandera.mosembro.Mosembro;

/** 
 * Holds configuration params for site-level search
 * It is used by /res/raw/search_form.js 
 */
public class SiteSearchInterface
{
    private Mosembro browser;
  
    public SiteSearchInterface(Mosembro browser)
    {
        this.browser = browser;
    }
    
    /**
     * Called by JavaScript in /res/raw/search_form.js as window.SiteSearchInterface.setSearchFormData(...). 
     * SiteSearchInterface is registered by Mosembro.onCreate().
     * 
     * @param scriptSecretKey A generated key which is available only to installed JS scripts. Prevents other scripts from calling this function. 
     * @param formAction Location where search form data is submitted to
     * @param inputName Name of the HTML field which accepts the search string
     * @param description Search form description
     */
    public void setSearchFormData(String scriptSecretKey, String formAction, String inputName, String description)
    {
        if (!browser.isValidScriptKey(scriptSecretKey)) {
            return;
        }
        
        String searchDescription = "Search site";
        if (description.length() > 1) {
            searchDescription = description;
        }
        
        HashMap<String, String> siteSearchConfig = new HashMap<String, String>();
        siteSearchConfig.put("formAction", formAction);
        siteSearchConfig.put("inputName", inputName);
        siteSearchConfig.put("searchDescription", searchDescription);
        browser.setSiteSearchOptions(true, siteSearchConfig);
        browser.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                browser.updateTitleIcons();
            }});
    }
    
}
