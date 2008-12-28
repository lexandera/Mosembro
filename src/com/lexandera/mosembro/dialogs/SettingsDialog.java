package com.lexandera.mosembro.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.lexandera.mosembro.Mosembro;
import com.lexandera.mosembro.R;

public class SettingsDialog extends Dialog
{
    public SettingsDialog(Context context, final Mosembro browser)
    {
        super(context);
        final SettingsDialog dialog = this;
        
        setContentView(R.layout.settings_dialog);
        
        final CheckBox enableLocationLinksCbox = (CheckBox)findViewById(R.id.enable_location_smart_links);
        enableLocationLinksCbox.setChecked(browser.getEnableLocationSmartLinks());
        
        final CheckBox enableEventLinksCbox = (CheckBox)findViewById(R.id.enable_event_smart_links);
        enableEventLinksCbox.setChecked(browser.getEnableEventSmartLinks());
        
        Button saveBtn = (Button)findViewById(R.id.settings_save);
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View arg0)
            {
                browser.setEnableEventSmartLinks(enableEventLinksCbox.isChecked());
                browser.setEnableLocationSmartLinks(enableLocationLinksCbox.isChecked());
                
                browser.savePreferences();
                dialog.dismiss();
            }
        });
    }
}
