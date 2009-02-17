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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lexandera.mosembro.Mosembro;
import com.lexandera.mosembro.R;

public class ManageActionsDialog extends Dialog
{
    Mosembro browser;
    final ArrayList<InstalledAction> installedActions = new ArrayList<InstalledAction>();
    
    public ManageActionsDialog(final Mosembro browser)
    {
        super(browser);
        setTitle("Manage installed actions");
        this.browser = browser;
        setContentView(R.layout.installed_actions_dialog);
               
        SQLiteDatabase db = browser.getActionStore().getReadableDatabase();
        Cursor data = db.rawQuery("SELECT action_id, name FROM actions", null);

        while (data.moveToNext()) {
            String id = data.getString(0);
            String name = data.getString(1);
            Bitmap icon = browser.getActionStore().getIconForAction(id);
            installedActions.add(new InstalledAction(id, name, icon));
        }
        
        InstalledActonsListArrayAdapter<InstalledAction> saAdapter = new InstalledActonsListArrayAdapter<InstalledAction>(
                browser, R.layout.smart_list_row, R.id.smart_list_text, installedActions);
        
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
    
    private class InstalledActonsListArrayAdapter<E extends InstalledAction> extends ArrayAdapter<E>
    {
        public InstalledActonsListArrayAdapter(Context context, int resource, int fieldId,
                List<E> objects)
        {
            super(context, resource, fieldId, objects);
        }
        
        public InstalledActonsListArrayAdapter(Context context, int resource, int fieldId,
                E[] objects)
        {
            super(context, resource, fieldId, objects);
        }
        
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            final InstalledAction ia = getItem(position);
            
            LinearLayout ll = new LinearLayout(super.getContext());
            ll.setPadding(8, 8, 4, 8);
            
            Button deleteBtn = new Button(super.getContext());
            deleteBtn.setText("X");
            ll.addView(deleteBtn);
            
            deleteBtn.setOnClickListener(new View.OnClickListener() 
            {
                @Override
                public void onClick(View v)
                {
                    new AlertDialog.Builder(browser)
                    .setTitle("Please confirm")
                    .setMessage("Really delete \"" + ia.getName() + "\"?")
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
            
            ImageView iv = new ImageView(super.getContext());
            iv.setImageBitmap(ia.getIcon());
            LinearLayout.LayoutParams iv_lp = new LinearLayout.LayoutParams(40, 40);
            iv.setLayoutParams(iv_lp);
            ll.addView(iv);
            
            TextView tv = new TextView(super.getContext());
            tv.setText(ia.getName());
            LinearLayout.LayoutParams tv_lp = new LinearLayout.LayoutParams(-1, -2);
            tv_lp.setMargins(16, 0, 0, 0);
            tv.setLayoutParams(tv_lp);
            ll.addView(tv);
            
            return ll;
        }
        
    }
    
}
