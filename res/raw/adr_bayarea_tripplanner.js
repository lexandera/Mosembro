
(function() {
    
    var AdrBayAreaTripPlanner = function() { };

    AdrBayAreaTripPlanner.prototype.process = function(data)
    {
        if (data['street-address'] != null && data['region'] != null) {
            if (data['region'].toLowerCase() == 'ca' || data['region'].toLowerCase() == 'california') {
                return {'intent-action': 'ACTION_VIEW',
                    'intent-url': "http://lexandera.com/mosembrodemo/bayarea_tripplanner.php" +
                                  "?dest_addr=" + encodeURIComponent(data['street-address']) +
                                  "&dest_city=" + encodeURIComponent(data['locality']),
                    'icon': 'com.lexandera.scripts.adr_bayarea_tripplanner',
                    'description-short': 'Travel to "' + data['street-address'] + '"',
                    'description-long': 'Travel to "' + data['street-address'] + '"'};
            }
        }
        
        return null;
    }
    
    return new AdrBayAreaTripPlanner();
})();