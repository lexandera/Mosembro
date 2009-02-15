package com.lexandera.mosembro;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lexandera.mosembro.dialogs.GoToDialog;
import com.lexandera.mosembro.dialogs.SettingsDialog;
import com.lexandera.mosembro.dialogs.SiteSearchDialog;
import com.lexandera.mosembro.dialogs.SmartActionsDialog;
import com.lexandera.mosembro.jsinterfaces.ActionInterface;
import com.lexandera.mosembro.jsinterfaces.SiteSearchInterface;
import com.lexandera.mosembro.util.MosembroUtil;

/**
 * Mosembro - Mobile semantic browser
 * 
 * The main parts are:
 * - JS interfaces which are used by injected JS code to pass data to the browser
 *   (each registered interface is then available to web pages as window.InterfaceName)
 * - JS scripts which are injected into loaded pages
 * - SmartActions which execute third party intents
 * 
 * A quick explanation of how it works:
 * 1. JS interfaces are registered in onCreate
 * 2. loadWebPage(...) is called at the end of onCreate
 * 3. When a page finishes loading, WebViewClient.onPageFinished() is called. 
 *    At this point JS files located in /res/raw/ are loaded and injected into the web page.
 * 4. JS code extracts microformats and passes the data to the browser using registered interfaces
 * 5. Interfaces create SmartActions which can then be executed by clicking on "smart links" (if enabled)
 *    or by going to "Menu > Smart actions"
 * */
public class Mosembro extends Activity {
    private WebView wv;
    private static final String PREFS_NAME = "smartBrowserPrefs";
    private boolean canSiteSearch = false;
    private HashMap<String, String> siteSearchConfig;
    private ArrayList<SmartAction> smartActions = new ArrayList<SmartAction>(10);
    private HashMap<String, ArrayList<SmartAction>> smartActionGroups = new HashMap<String, ArrayList<SmartAction>>(10);
    private ActionStore actionStore;
    private MenuItem searchMenuItem;
    private MenuItem microformatsMenuItem;
    private boolean enableContentRewriting;
    private String lastTargetURL = "";
    
    static final int MENU_GO_TO = 1;
    static final int MENU_RELOAD = 2;
    static final int MENU_SITE_SEARCH = 3;
    static final int MENU_SMART_ACTIONS = 4;
    static final int MENU_SETTINGS = 5;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        getWindow().requestFeature(Window.FEATURE_RIGHT_ICON);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        
        setContentView(R.layout.main);
        updateTitleIcons();
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        enableContentRewriting = settings.getBoolean("enableContentRewriting", true);
        
        actionStore = new ActionStore(this);
        actionStore.updateBuiltInActions();
        
        wv = (WebView)findViewById(R.id.browser);
        
        WebSettings websettings = wv.getSettings();
        websettings.setJavaScriptEnabled(true);
        
        /* Enable zooming */
        websettings.setSupportZoom(true);
        FrameLayout zoomholder = (FrameLayout)this.findViewById(R.id.browser_zoom_controls);
        zoomholder.addView(wv.getZoomControls());
        wv.getZoomControls().setVisibility(View.GONE);

        /* Register JS interfaces used by scripts located in /res/raw/ */
        wv.addJavascriptInterface(new ActionInterface(this), "ActionInterface");
        wv.addJavascriptInterface(new SiteSearchInterface(this), "SiteSearchInterface");
        
        wv.setWebViewClient(new WebViewClient()
        {
            /** 
             * This method is called after a page finishes loading.
             * 
             * It reads all the JS files and injects them into the web page which has just 
             * finished loading. This is achieved by calling loadUrl("javascript:<js-code-here>"),
             * which is the exact same method used by bookmarklets.
             */
            @Override
            public void onPageFinished(WebView view, String url)
            {
                String commonJS = getScript(R.raw.common);
                String[] scripts = {getScript(R.raw.search_form), getScript(R.raw.parser_adr), getScript(R.raw.parser_vevent)};
                
                for (String script : scripts) {
                    getWebView().loadUrl("javascript:(function(){ " + 
                                         commonJS + " " +
                                         script + " })()");
                }

                super.onPageFinished(view, url);
            }
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                setSiteSearchOptions(false, null);
                resetSmartActions();
                
                super.onPageStarted(view, url, favicon);
            }
        });
        
        wv.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) 
            {
                new AlertDialog.Builder(Mosembro.this)
                    .setTitle(R.string.title_dialog_alert)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() 
                            {
                                public void onClick(DialogInterface dialog, int which) 
                                {
                                    result.confirm();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();
                
                return true;
            };
            
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) 
            {
                new AlertDialog.Builder(Mosembro.this)
                    .setTitle(R.string.title_dialog_confirm)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, 
                            new DialogInterface.OnClickListener() 
                            {
                                public void onClick(DialogInterface dialog, int which) 
                                {
                                    result.confirm();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel, 
                            new DialogInterface.OnClickListener() 
                            {
                                public void onClick(DialogInterface dialog, int which) 
                                {
                                    result.cancel();
                                }
                            })
                .create()
                .show();
            
                return true;
            };
            
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) 
            {
                final LayoutInflater factory = LayoutInflater.from(Mosembro.this);
                final View v = factory.inflate(R.layout.javascript_prompt_dialog, null);
                ((TextView)v.findViewById(R.id.prompt_message_text)).setText(message);
                ((EditText)v.findViewById(R.id.prompt_input_field)).setText(defaultValue);

                new AlertDialog.Builder(Mosembro.this)
                    .setTitle(R.string.title_dialog_prompt)
                    .setView(v)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String value = ((EditText)v.findViewById(R.id.prompt_input_field)).getText()
                                            .toString();
                                    result.confirm(value);
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    result.cancel();
                                }
                            })
                    .setOnCancelListener(
                            new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                    result.cancel();
                                }
                            })
                    .show();
                
                return true;
            };

            
            
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                updateProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }
            
            @Override
            public void onReceivedTitle(WebView view, String title)
            {
                setTitle(title);
                super.onReceivedTitle(view, title);
            }
        });
        
        
        //loadWebPage("http://10.0.2.2/");
        loadWebPage("http://lexandera.com/mosembrodemo/");
    }
    
    public void loadWebPage(String targetURL)
    {
        if (targetURL == null) {
            return;
        }
        
        /* Fix URL if it doesn't begin with 'http' or 'file:'. 
         * WebView will not load URLs which do not specify protocol. */
        if (targetURL.indexOf("http") != 0 && targetURL.indexOf("file:") != 0) {
            targetURL = "http://" + targetURL;
        }
        
        lastTargetURL = targetURL;
        setTitle("Loading "+targetURL);
        
        setSiteSearchOptions(false, null);
        resetSmartActions();
        
        getWebView().loadUrl(targetURL);
    }
    
    public WebView getWebView()
    {
        return wv;
    }

    public void setSiteSearchOptions(boolean canSiteSearch, HashMap<String, String> config)
    {
        this.canSiteSearch = canSiteSearch;
        this.siteSearchConfig = config;
    }
    
    public int addSmartAction(SmartAction sa, int groupId)
    {
        String groupKey = "actionGroup" + Integer.toString(groupId);
        
        /* create group if it doesn't exist yet */
        if (!smartActionGroups.containsKey(groupKey)) {
            smartActionGroups.put(groupKey, new ArrayList<SmartAction>(5));
        }
        
        smartActions.add(sa);
        smartActionGroups.get(groupKey).add(sa);
        
        return smartActions.size() -1;
    }
    
    public void resetSmartActions()
    {
        smartActions = new ArrayList<SmartAction>(10);
        smartActionGroups = new HashMap<String, ArrayList<SmartAction>>(10);
        updateTitleIcons();
    }
    
    public ArrayList<SmartAction> getSmartActions()
    {
        return smartActions;
    }
    
    public ArrayList<SmartAction> getSmartActionsForGroup(int groupId)
    {
        String groupKey = "actionGroup" + Integer.toString(groupId);

        if (smartActionGroups.containsKey(groupKey)) {
            return smartActionGroups.get(groupKey);
        }
        
        return null;
    }
    
    public boolean getEnableContentRewriting()
    {
        return enableContentRewriting;
    }
    
    public void setEnableContentRewriting(boolean enable)
    {
        enableContentRewriting = enable;
    }
    
    public String getLastUrl()
    {
        return lastTargetURL;
    }
    
    public void updateProgress(int progress)
    {
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, progress * 100);
    }
    
    @Override
    protected void onStop()
    {
        super.onStop();
        
        savePreferences();
    }
    
    public void savePreferences()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("enableContentRewriting", enableContentRewriting);
        editor.commit();
    }
    
    /**
     * Changes actiove/inactive state of icons in the title bar 
     */
    public void updateTitleIcons()
    {
        if (this.smartActions.size() > 0) {
            getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.title_mf_ico);
        }
        else {
            getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.title_mf_ico_disabled);
        }
        
        if (this.canSiteSearch) {
            getWindow().setFeatureDrawableResource(Window.FEATURE_RIGHT_ICON, R.drawable.title_search_ico);
        }
        else {
            getWindow().setFeatureDrawableResource(Window.FEATURE_RIGHT_ICON, R.drawable.title_search_ico_disabled);
        }
    }
    
    /** 
     * Reads a script form a javascript file located in /res/raw/ 
     * and retuns it as a String.
     */
    public String getScript(int resourceId)
    {
        return MosembroUtil.readRawString(getResources(), resourceId);
    }
    
    public ActionStore getActionStore()
    {
        return actionStore;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        
        MenuItem menuItem;
        
        menuItem = menu.add(Menu.NONE, MENU_GO_TO, Menu.NONE, R.string.menu_go_to);
        menuItem.setIcon(R.drawable.menu_go_to);
        
        menuItem = menu.add(Menu.NONE, MENU_RELOAD, Menu.NONE, R.string.menu_reload);
        menuItem.setIcon(R.drawable.menu_refresh);
        
        microformatsMenuItem = menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, R.string.menu_settings);
        microformatsMenuItem.setIcon(R.drawable.menu_microformats_settings);
        
        searchMenuItem = menu.add(Menu.NONE, MENU_SITE_SEARCH, Menu.NONE, R.string.menu_search_site);
        searchMenuItem.setIcon(R.drawable.menu_site_search2_disabled);
        
        microformatsMenuItem = menu.add(Menu.NONE, MENU_SMART_ACTIONS, Menu.NONE, R.string.menu_smart_actions);
        microformatsMenuItem.setIcon(R.drawable.menu_microformats3_disabled);
        
        return true;
    }
    
    /**
     * Changes enabled/disabled state of menu items
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if (canSiteSearch) {
            searchMenuItem.setIcon(R.drawable.menu_site_search2);
            searchMenuItem.setEnabled(true);
        }
        else {
            searchMenuItem.setIcon(R.drawable.menu_site_search2_disabled);
            searchMenuItem.setEnabled(false);
        }
        
        if (smartActions.size() > 0) {
            microformatsMenuItem.setIcon(R.drawable.menu_microformats3);
            microformatsMenuItem.setEnabled(true);
        }
        else {
            microformatsMenuItem.setIcon(R.drawable.menu_microformats3_disabled);
            microformatsMenuItem.setEnabled(false);
        }
        
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case MENU_GO_TO:
                /* open URL dialog */
                new GoToDialog(this, this).show();
                return true;
                
            case MENU_RELOAD:
                /* reload */
                loadWebPage(lastTargetURL);
                return true;
                
            case MENU_SITE_SEARCH:
                /* site search */
                if (canSiteSearch) {
                    new SiteSearchDialog(this, this, siteSearchConfig).show();
                }
                return true;
                
            case MENU_SMART_ACTIONS:
                /* microformats */
                if (smartActions.size() > 0) {
                    new SmartActionsDialog(this, this).show();
                }
                return true;
                
            case MENU_SETTINGS:
                new SettingsDialog(this, this).show();
                return true;
        }
                
        return false;
    }
    

    
}