package com.lexandera.mosembro.dialogs;

import android.app.Dialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lexandera.mosembro.Mosembro;
import com.lexandera.mosembro.R;

public class GoToDialog extends Dialog
{
    Mosembro browser;
    EditText urlField;
    
    public GoToDialog(final Mosembro browser)
    {
        super(browser);
        
        this.browser = browser;
        final GoToDialog dialog = this;
        
        setContentView(R.layout.go_to_dialog);
        setTitle("Open URL");
        
        urlField = (EditText)findViewById(R.id.go_to_url_field);
        urlField.setText(browser.getLastEnteredUrl());

        urlField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    dialog.dismiss();
                    dialog.loadWebPage(urlField.getText().toString());
                    
                    return true;
                }
                
                return false;
            }
        });
        

        Button clearBtn = (Button)findViewById(R.id.go_to_clear_button);
        clearBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                urlField.setText("");
            }
        });
        
        Button goBtn = (Button)findViewById(R.id.go_to_go_button);
        goBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                dialog.dismiss();
                dialog.loadWebPage(urlField.getText().toString());
            }
        });
    }
    
    public void loadWebPage(String url)
    {
        browser.loadWebPage(url);
    }
}
