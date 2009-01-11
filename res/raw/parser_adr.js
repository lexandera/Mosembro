
/* Locates 'adr' elements and extracts location information from them */
(function() {
	var scripts;
	eval("scripts = " + window.ActionInterface.getScriptsFor('adr'));
	
	if (scripts.length == 0) {
		return;
	}
	
	var addrs = getElementsByAttribute(document.body, '*', 'class', 'adr');
    
    for (var i = 0; i < addrs.length; i++) {
    	var addr = addrs[i];
    	
    	var streets = getElementsByAttribute(addr, '*', 'class', 'street-address');
    	var localities = getElementsByAttribute(addr, '*', 'class', 'locality');
    	var postalCodes = getElementsByAttribute(addr, '*', 'class', 'postal-code');

    	var microformatData = {'street-address': streets[0].innerHTML,
    			               'locality': localities[0].innerHTML,
    			               'postal-code': postalCodes[0].innerHTML};
    	
    	for (var x=0; x<scripts.length; x++) {
    		var obj;
    		eval("obj = " + scripts[x]);
    		var actionData = obj.process(microformatData);
    		
    		if (actionData) {
    			var link = window.ActionInterface.addAction(actionData['intent-action'], 
									    					actionData['intent-url'], 
									    					actionData['icon'], 
									    					actionData['description-short'],
									    					actionData['description-long']);
    			
    			if (link) {
    				addr.innerHTML += link;
    			}
    		}
    	}
    }
	
})();