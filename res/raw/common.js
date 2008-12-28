
/* Common support functions for scripts */

function getElementsByAttribute(topElement, tagName, attrName, attrValue)
{
    var candidateElms = topElement.getElementsByTagName(tagName);
    var attrValue = new RegExp("(^|\\s)" + attrValue + "(\\s|$)", "i");
    var out = new Array();
    
    for (var i=0; i<candidateElms.length; i++) {
        var currentElm = candidateElms[i];
        var attr = currentElm.getAttribute(attrName);
        
        if (typeof attr == "string" && 
            attr.length > 0 && 
            attrValue.test(attr)) 
        {
            out.push(currentElm);
        }
    }
    
    return out;
}
