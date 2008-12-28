package com.lexandera.mosembro;

public abstract class SmartAction
{
    abstract public void execute();
    
    abstract public String getLongDescription();
    
    abstract public String getShortDescription();
    
    abstract public int getIconResourceid();
    
    @Override
    public String toString()
    {
        return getLongDescription();
    }
}
