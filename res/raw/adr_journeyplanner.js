// ==Action==
// @name           London journey planner
// @id             com.lexandera.scripts.LondonJourneyPlanner
// @type           microformat
// @handles        adr
// ==/Action==

(function() {
    var action = function() { };
    action.id = 'com.lexandera.scripts.LondonJourneyPlanner';
    action.process = function(data, matchedNode)
    {
        if (data['street-address'] != null && data['locality'] != null) {
            if (data['locality'].toLowerCase() == 'london') {
                var fullAddr = data['street-address']
                                    + ", " + data['locality']
                                    + (data['postal-code'] != null ? ", " + data['postal-code'] : "");
            
                return {'intent-action': 'ACTION_VIEW',
                    'content': "http://lexandera.com/mosembrodemo/tflplanner.php?dest=" + encodeURIComponent(fullAddr),
                    'description-short': 'Travel to "' + data['street-address'] + '"',
                    'description-long': 'Travel to "' + data['street-address'] + '"'};
            }
        }
        
        return null;
    }
    
    return action;
})();