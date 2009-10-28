package com.lexandera.mosembro.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.lexandera.mosembro.Mosembro;
import com.lexandera.mosembro.R;
import com.lexandera.mosembro.SmartAction;

/**
 * This dialog displays a list of actions available for currently loaded page
 */
public class SmartActionsDialog extends Dialog
{
    Mosembro browser;
    ListView saList;
    public static final int DISPLAY_ALL = 1;
    
    /**
     * 
     * @param browser
     * @param actionGroup ID of the group of actions to display. If equal to DISPLAY_ALL, all actions will be displayed. 
     */
    public SmartActionsDialog(final Mosembro browser, int actionGroup)
    {
        super(browser);
        init(browser, actionGroup);
    }
    
    public SmartActionsDialog(final Mosembro browser)
    {
        super(browser);
        init(browser, DISPLAY_ALL);
    }
    
    /**
     * 
     * @param browser
     * @param actionGroup ID of the group of actions to display. If equal to DISPLAY_ALL, all actions will be displayed. 
     */
    void init(final Mosembro browser, int actionGroup)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.smart_actions_dialog);

        this.browser = browser;
        final SmartActionsDialog dialog = this;
        ArrayList<SmartAction> actions;
        
        if (actionGroup < 0) {
            actions = browser.getSmartActions();
        }
        else {
            actions = browser.getSmartActionsForGroup(actionGroup);
        }
        
        if (actions == null) {
            return;
        }
        
        final ArrayList<SmartAction> finalActions = actions;
        SmartListArrayAdapter saAdapter = new SmartListArrayAdapter(browser, actions);
       
        saList = (ListView)findViewById(R.id.smart_actions_list);
        saList.setAdapter(saAdapter);
        saList.setOnItemClickListener(new OnItemClickListener()
        {
            @SuppressWarnings("unchecked")
            public void onItemClick(AdapterView parent, View v, int position, long id)
            {
                dialog.dismiss();
                finalActions.get(position).execute();
            }
        });
    }
    
    private class SmartListArrayAdapter extends ArrayAdapter<SmartAction>
    {
        public SmartListArrayAdapter(Context context, List<SmartAction> objects)
        {
            super(context, 0, objects);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
        	TextView tv;

            if (convertView == null) {
                tv = new TextView(super.getContext());
                tv.setPadding(8, 8, 4, 8);
            } 
            else {
                tv = (TextView) convertView;
            }
        	
            SmartAction sa = getItem(position);
            
            tv.setText(sa.toString());
            tv.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(sa.getIconBitmap()), null, null, null);
            tv.setCompoundDrawablePadding(10);
            
            return tv;
        }
    }
    
}
