package com.lexandera.mosembro.jsinterfaces;

import com.lexandera.mosembro.Mosembro;

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
}
