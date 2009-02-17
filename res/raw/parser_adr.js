
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
        
        var showLink = false;
        var groupId = window.ActionInterface.startNewActionGroup();
        
        for (var x=0; x<scripts.length; x++) {
            var obj;
            eval("obj = " + scripts[x]);
            var actionData = obj.process(microformatData, addr);
            
            if (actionData) {
                var link = window.ActionInterface.addAction(obj.id,
                                                            actionData['intent-action'], 
                                                            actionData['intent-url'], 
                                                            actionData['description-short'],
                                                            actionData['description-long']);
                
                if (link) {
                    showLink = true;
                }
            }
        }
        
        if (showLink) {
            var txt = microformatData['street-address'];
            if (txt.length > 23) {
                txt = txt.substring(0, 20) + '...';
            }
            addr.innerHTML += window.ActionInterface.actionGroupLink(groupId, 'Actions for "'+txt+'"');
        }
    }
    
})();