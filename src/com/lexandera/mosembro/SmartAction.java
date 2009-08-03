package com.lexandera.mosembro;

import android.graphics.Bitmap;

/**
 * A prototype for dynamically generated classes for content-related smart actions.
 * @see ActionInterface.addAction()
 */
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
