// ==Action==
// @name           Bay area trip planner
// @id             com.lexandera.scripts.BayAreaTripPlanner
// @type           microformat
// @handles        adr
// ==/Action==

(function() {
    var action = function() { };
    action.id = 'com.lexandera.scripts.BayAreaTripPlanner';
    action.process = function(data, matchedNode)
    {
        if (data['street-address'] != null && data['region'] != null) {
            if (data['region'].toLowerCase() == 'ca' || data['region'].toLowerCase() == 'california') {
                return {'intent-action': 'ACTION_VIEW',
                    'content': "http://lexandera.com/mosembrodemo/bayarea_tripplanner.php" +
                                  "?dest_addr=" + encodeURIComponent(data['street-address']) +
                                  "&dest_city=" + encodeURIComponent(data['locality']),
                    'description-short': 'Travel to "' + data['street-address'] + '"',
                    'description-long': 'Travel to "' + data['street-address'] + '"'};
            }
        }
        
        return null;
    }
    
    return action;
})();