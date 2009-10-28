package com.lexandera.mosembro.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.lexandera.mosembro.Mosembro;
import com.lexandera.mosembro.R;

public class ManageActionsDialog extends Dialog
{
    Mosembro browser;
    
    public ManageActionsDialog(final Mosembro browser)
    {
        super(browser);
        setTitle("Manage installed actions");
        setContentView(R.layout.installed_actions_dialog);
               
        this.browser = browser;
        ArrayList<InstalledAction> installedActions = new ArrayList<InstalledAction>();
        SQLiteDatabase db = browser.getActionStore().getReadableDatabase();
        Cursor data = db.rawQuery("SELECT action_id, name FROM actions", null);

        while (data.moveToNext()) {
            String id = data.getString(0);
            String name = data.getString(1);
            Bitmap icon = browser.getActionStore().getIconForAction(id);
            installedActions.add(new InstalledAction(id, name, icon));
        }
        
        InstalledActonsListArrayAdapter saAdapter = 
            new InstalledActonsListArrayAdapter(browser, installedActions);
        
        ListView installedActionsList;
        installedActionsList = (ListView)findViewById(R.id.installed_actions_list);
        installedActionsList.setAdapter(saAdapter);
    }
    
    private class InstalledAction
    {
        private String id;
        private String name;
        private Bitmap icon;
        
        public InstalledAction(String id, String name, Bitmap icon)
        {
            this.id = id;
            this.name = name;
            this.icon = icon;
        }
        
        public String getId()
        {
            return id;
        }
        
        public String getName()
        {
            return name;
        }
        
        public Bitmap getIcon()
        {
            return icon;
        }
    }
    
    private class InstalledActonsListArrayAdapter extends ArrayAdapter<InstalledAction>
    {
        public InstalledActonsListArrayAdapter(Context context, List<InstalledAction> objects)
        {
            super(context, 0, objects);
        }
        
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
        	ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                LinearLayout ll = new LinearLayout(super.getContext());
                ll.setPadding(8, 8, 4, 8);

                Button deleteBtn = new Button(super.getContext());
                deleteBtn.setText("X");
                ll.addView(deleteBtn);

                TextView tv = new TextView(super.getContext());
                LinearLayout.LayoutParams tv_lp = new LayoutParams(
                        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
                tv_lp.setMargins(16, 0, 0, 0);
                tv.setLayoutParams(tv_lp);
                ll.addView(tv);

                holder.label = tv;
                holder.deleteBtn = deleteBtn;
                ll.setTag(holder);

                convertView = ll;
            } 
            else {
                holder = (ViewHolder) convertView.getTag();
            }
        	
            final InstalledAction ia = getItem(position);

            holder.label.setText(ia.getName());
            holder.label.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(ia.getIcon()), null, null, null);
            holder.label.setCompoundDrawablePadding(10);
            
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() 
            {
                @Override
                public void onClick(View v)
                {
                    String confirmMsg = browser.getResources().getString(R.string.action_delete_confirm_dialog_msg);
                    new AlertDialog.Builder(browser)
                        .setTitle(R.string.action_delete_confirm_dialog_title)
                        .setMessage(String.format(confirmMsg, ia.getName()))
                        .setPositiveButton(android.R.string.yes, 
                                new DialogInterface.OnClickListener() 
                                {
                                    public void onClick(DialogInterface dialog, int which) 
                                    {
                                        browser.getActionStore().deleteAction(ia.getId());
                                        remove(getItem(position));
                                    }
                                })
                        .setNegativeButton(android.R.string.no, null)
                    .create()
                    .show();
                }
            });
            
            return convertView;
        }
        
        class ViewHolder 
        {
        	TextView label;
        	Button deleteBtn;
        }
    }
    
}
