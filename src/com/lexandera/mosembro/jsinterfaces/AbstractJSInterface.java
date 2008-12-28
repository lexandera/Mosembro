package com.lexandera.mosembro.jsinterfaces;

public abstract class AbstractJSInterface
{
    public String actionLink(int actionId, String text)
    {
        return "<div style=\"display: block; clear: both; margin: 5px 5px 5px 2px;\">"+
            "<a href=\"/null\" " +
            "onclick=\"window.ActionInterface.executeAction("+Integer.toString(actionId)+"); " +
        	"return false;\">" + text + 
        	"</a>" +
        	"</div>";
    }
}
