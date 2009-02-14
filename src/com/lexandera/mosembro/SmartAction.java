package com.lexandera.mosembro;

import android.graphics.Bitmap;

public abstract class SmartAction
{
    abstract public void execute();
    
    abstract public String getLongDescription();
    
    abstract public String getShortDescription();
    
    abstract public Bitmap getIconBitmap();

    @Override
    public String toString()
    {
        return getLongDescription();
    }

}
