package com.lexandera.mosembro.dialogs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lexandera.mosembro.Mosembro;
import com.lexandera.mosembro.R;

public class SiteSearchDialog extends Dialog
{
    Mosembro browser;
    EditText searchField;
    String formAction = "";
    String inputName = "";
    
    public SiteSearchDialog(Context context, Mosembro browser, HashMap<String, String> siteSearchConfig)
    {
        super(context);
        setContentView(R.layout.search_dialog);
        final SiteSearchDialog dialog = this;
        this.formAction = siteSearchConfig.get("formAction");
        this.inputName = siteSearchConfig.get("inputName");
        setTitle(siteSearchConfig.get("searchDescription"));
        
        this.browser = browser;
        searchField = (EditText)findViewById(R.id.search_field);
        searchField.setText("");
        
        searchField.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    dialog.hide();
                    dialog.doSearch(searchField.getText().toString());
                    
                    return true;
                }
                
                return false;
            }
        });
        
        Button searchBtn = (Button)findViewById(R.id.search_button);
        searchBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                dialog.hide();
                dialog.doSearch(searchField.getText().toString());
            }
        });
    }
    
    public void doSearch(String search)
    {
        String url;
        
        // FIXME : DOES NOT WORK WITH RELATIVE URLs!!!!
        
        try {
            url = formAction + (formAction.indexOf("?") >= 0 ? "&" : "?") + inputName
                    + "=" + URLEncoder.encode(search, "UTF-8");
            
            browser.loadWebPage(url);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
    }
    
}
