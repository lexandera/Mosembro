// ==Action==
// @name           Show address on map
// @id             com.lexandera.scripts.AddressToGMap
// @type           microformat
// @handles        adr
// ==/Action==

(function() {
    var action = function() { };
    action.id = 'com.lexandera.scripts.AddressToGMap';
    action.process = function(data, matchedNode)
    {
        if (data['street-address'] != null && (data['locality'] != null || data['postal-code'] != null)) {
            var fullAddr = data['street-address']
                    + (data['locality'] != null ? ", " + data['locality'] : "")
                    + (data['postal-code'] != null ? ", " + data['postal-code'] : "");
            
            return {'intent-action': 'ACTION_VIEW',
                    'content': "geo:0,0?q=" + encodeURIComponent(fullAddr),
                    'description-short': 'Show "' + data['street-address'] + '" on map',
                    'description-long': 'Show "' + data['street-address'] + '" using maps application'};
        }
        
        return null;
    }
    
    return action;
})();
