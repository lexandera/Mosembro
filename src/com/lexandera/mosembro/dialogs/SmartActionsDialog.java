package com.lexandera.mosembro.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.lexandera.mosembro.Mosembro;
import com.lexandera.mosembro.R;
import com.lexandera.mosembro.SmartAction;

public class SmartActionsDialog extends Dialog
{
    Mosembro browser;
    ListView saList;
    
    public SmartActionsDialog(final Mosembro browser, int actionGroup)
    {
        super(browser);
        init(browser, actionGroup);
    }
    
    public SmartActionsDialog(final Mosembro browser)
    {
        super(browser);
        init(browser, -1);
    }
    
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
        SmartListArrayAdapter<SmartAction> saAdapter = new SmartListArrayAdapter<SmartAction>(browser, actions);
       
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
    
    private class SmartListArrayAdapter<E extends SmartAction> extends ArrayAdapter<E>
    {
        public SmartListArrayAdapter(Context context, List<E> objects)
        {
            super(context, 0, objects);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            SmartAction sa = getItem(position);
            
            LinearLayout ll = new LinearLayout(super.getContext());
            ll.setPadding(8, 8, 4, 8);
            
            LinearLayout.LayoutParams iv_lp = new LinearLayout.LayoutParams(40, 40);
            ImageView iv = new ImageView(super.getContext());
            iv.setImageBitmap(sa.getIconBitmap());
            iv.setLayoutParams(iv_lp);
            ll.addView(iv);
            
            LinearLayout.LayoutParams tv_lp = new LinearLayout.LayoutParams(-1, -2);
            tv_lp.setMargins(16, 0, 0, 0);
            TextView tv = new TextView(super.getContext());
            tv.setText(sa.toString());
            tv.setLayoutParams(tv_lp);
            ll.addView(tv);
            
            return ll;
        }
        
    }
    
}
