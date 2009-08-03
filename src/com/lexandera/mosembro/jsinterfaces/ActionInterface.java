package com.lexandera.mosembro.jsinterfaces;

import org.json.JSONArray;

import com.lexandera.mosembro.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.ClipboardManager;
import android.widget.Toast;

import com.lexandera.mosembro.Mosembro;
import com.lexandera.mosembro.SmartAction;
import com.lexandera.mosembro.dialogs.SmartActionsDialog;

/** 
 * This JS interface handles adding of new actions and window.ActionInterface.execute(id) 
 * calls which are triggered by onclick events, attached to "smart links" (when smart links are enabled)
 */
public class ActionInterface
{
    Mosembro browser;
    int actionGroupId = 0;
    
    public ActionInterface(Mosembro browser)
    {
        this.browser = browser;
    }
    
    /**
     * Returns all installed scripts for a certain category. This is used by content parsers written in JS.
     * @param scriptSecretKey A generated key which is available only to installed JS scripts. Prevents other scripts from calling this function. 
     * @param category For now this is the name of a supported Microformat (adr, vevent, ...)
     * @return a string representation of a JSON array containing installed scripts for requested category
     */
    public String getScriptsFor(String scriptSecretKey, String category)
    {
        if (!browser.isValidScriptKey(scriptSecretKey)) {
            return "";
        }
        
        JSONArray jsa = new JSONArray();
        
        String[] actions = browser.getActionStore().getStriptsForMicroformatActions(category);
        for (int i=0; i<actions.length; i++) {
            jsa.put(actions[i]);
        }
        
        return jsa.toString();
    }

    /**
     * Starts a new group for actions. All actions attached to the same link belong to one group. 
     * @param scriptSecretKey A generated key which is available only to installed JS scripts. Prevents other scripts from calling this function. 
     * @return ID of new group
     */
    public int startNewActionGroup(String scriptSecretKey)
    {
        if (!browser.isValidScriptKey(scriptSecretKey)) {
            return 0;
        }
        
        return ++actionGroupId;
    }
    
    /**
     * Adds a new content-related smart action
     * 
     * @param scriptSecretKey A generated key which is available only to installed JS scripts. Prevents other scripts from calling this function.
     * @param actionId Action script's internal identification string (script's @id field)
     * @param action Intent action (TEXT_COPY, RUN_JAVASCRIPT, or any of Intent class' ACTION_* fields like ACTION_VIEW, ...)
     * @param value Intent URI
     * @param descShort Short description of what this action does
     * @param descLong Long description of what this action does
     * @return true if it is OK to display a link for this action, false otherwise
     */
    public boolean addAction(String scriptSecretKey, 
            final String actionId, final String action, final String value, 
            final String descShort, final String descLong)
    {
        if (!browser.isValidScriptKey(scriptSecretKey)) {
            return false;
        }
        
        /**
         * Selecting an action from a list will trigger this instance's execute() method
         */
        final SmartAction sa = new SmartAction()
        {
            @Override
            public void execute()
            {
                String intentAction = null;
                
                if ("TEXT_COPY".equals(action)) {
                    ClipboardManager clipboard = (ClipboardManager)browser.getSystemService(Context.CLIPBOARD_SERVICE); 
                    clipboard.setText(value);
                }
                else if ("RUN_JAVASCRIPT".equals(action)) {
                    browser.getWebView().loadUrl("javascript:(function(){ " + value + " })()");
                }
                else {
                    try {
                        intentAction = (String)Intent.class.getField(action).get(null);
                        Intent i = new Intent(intentAction, Uri.parse(value));
                        browser.startActivity(i);
                    }
                    catch (Exception e) {
                    	Toast.makeText(browser, R.string.action_execute_failed, Toast.LENGTH_SHORT).show();
                    }
                }
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
            public Bitmap getIconBitmap()
            {
                return browser.getActionStore().getIconForAction(actionId);
            }
        };
        
        browser.addSmartAction(sa, actionGroupId);
        browser.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                browser.updateTitleIcons();
            }});
        
        if (browser.getEnableContentRewriting()) {
            return true;
        };
        
        return false;
    }
    
    /**
     * Displays a dialog containing a list of available content-related actions.
     * Usually called by JS generated in ActionInterface.actionGroupLink().
     * @param groupId ID of group of actions to show
     */
    public void showActionGroupDialog(int groupId)
    {
        new SmartActionsDialog(browser, groupId).show();
    }
    
    /**
     * Generates HTML for a link which displays a dialog with available actions when clicked
     * @param scriptSecretKey A generated key which is available only to installed JS scripts. Prevents other scripts from calling this function. 
     * @param groupId ID of group to display when clicked
     * @param text Link text
     * @return string containing HTML
     */
    public String actionGroupLink(String scriptSecretKey, int groupId, String text)
    {
        if (!browser.isValidScriptKey(scriptSecretKey)) {
            return "";
        }
        
        // NOTE: DO NOT put scriptSecretKey in this link because that would make it 
        //       possible for any piece of JavaScript to get it!
        return "<div style=\"display: block; clear: both; margin: 5px 5px 5px 2px; font-size: 85%;\">"+
            "<a href=\"/null\" " +
            "onclick=\"window.ActionInterface.showActionGroupDialog("+Integer.toString(groupId)+"); " +
            "return false;\">" + text + 
            "</a>" +
            "</div>";
    }
}
