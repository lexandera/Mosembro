
/* Locates 'vevent' elements and extracts event information from them, which it then passes to EventToGcalInterface  */
(function() {
    var events = getElementsByAttribute(document.body, '*', 'class', 'vevent');
    
    for (var i = 0; i < events.length; i++) {
    	var event = events[i];
    	
    	var eventLocation = getElementsByAttribute(event, '*', 'class', 'location');
    	var eventSummary = getElementsByAttribute(event, '*', 'class', 'summary');
    	var startDate = getElementsByAttribute(event, '*', 'class', 'dtstart');
    	var endDate = getElementsByAttribute(event, '*', 'class', 'dtend');

    	if (eventLocation.length > 0) {
    		eventLocation = eventLocation[0].innerHTML;
    	}
    	if (eventSummary.length > 0) {
    		eventSummary = eventSummary[0].innerHTML;
    	}
    	if (startDate.length > 0) {
    		startDate = startDate[0].getAttribute('title');
    	}
    	if (endDate.length > 0) {
    		endDate = endDate[0].getAttribute('title');
    	}
    	if (!endDate) {
    		endDate = startDate;
    	}
    	
    	var appendHTML;
    	if (appendHTML = window.EventToGcalInterface.addEvent(eventLocation, eventSummary, startDate, endDate)) {
    		/* rewrite contents - append action link */
    		event.innerHTML = event.innerHTML + appendHTML;
    	}
    	
    }
})();
