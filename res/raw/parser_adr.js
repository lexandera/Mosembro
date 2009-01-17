
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
    	var regions = getElementsByAttribute(addr, '*', 'class', 'region');
    	var countries = getElementsByAttribute(addr, '*', 'class', 'country-name');

    	var microformatData = {'street-address': (streets[0] ? streets[0].innerHTML : null),
    			               'locality': (localities[0] ? localities[0].innerHTML : null),
    			               'postal-code': (postalCodes[0] ? postalCodes[0].innerHTML : null),
    			               'region': (regions[0] ? regions[0].innerHTML : null),
    			               'country-name': (countries[0] ? countries[0].innerHTML : null)};
    	
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