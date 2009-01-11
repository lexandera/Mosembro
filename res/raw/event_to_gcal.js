
/* Locates 'vevent' elements and extracts event information from them */

(function() {
	var EventToGcal = function() { };

	EventToGcal.prototype.process = function(data)
	{
    	if (data['summary'] != null && data['dtstart'] != null && data['dtend'] != null) {
            var addLink = "http://www.google.com/calendar/event?action=TEMPLATE"
                + "&text=" + encodeURIComponent(data['summary'])
                + "&dates=" + data['dtstart'] + "/" + data['dtend']
                + (data['location'] != null ? "&location=" + encodeURIComponent(data['location']) : "");
    		
	        return {'intent-action': 'ACTION_VIEW',
	    	        'intent-url': addLink,
	                'icon': 'calendar',
	                'description-short': 'Add "' + eventSummary + '" to my calendar',
	                'description-long': 'Add "' + eventSummary + '" to my Google calendar'};
    	}
    	

		return null;
	}
	
	return new EventToGcal();
})();

