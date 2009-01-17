
(function() {
	
	var AdrJourneyPlanner = function() { };

	AdrJourneyPlanner.prototype.process = function(data)
	{
        if (data['street-address'] != null && data['locality'] != null) {
        	if (data['locality'].toLowerCase() == 'london') {
        		var fullAddr = data['street-address']
        		                    + ", " + data['locality']
        		                    + (data['postal-code'] != null ? ", " + data['postal-code'] : "");
        	
                return {'intent-action': 'ACTION_VIEW',
            		'intent-url': "http://lexandera.com/mosembrodemo/tflplanner.php?dest=" + encodeURIComponent(fullAddr),
            		'icon': 'journeyplanner',
            		'description-short': 'Travel to "' + data['street-address'] + '"',
            		'description-long': 'travel to "' + data['street-address'] + '"'};
        	}
        }
		
		return null;
	}
	
	return new AdrJourneyPlanner();
})();