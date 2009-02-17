// ==Action==
// @name           Add event to Google calendar
// @id             com.lexandera.scripts.EventToGCal
// @type           microformat
// @handles        vevent
// ==/Action==

(function() {
    var action = function() { };
    action.id = 'com.lexandera.scripts.EventToGCal';
    action.process = function(data, matchedNode)
    {
        if (data['summary'] != null && data['dtstart'] != null && data['dtend'] != null) {
            var addLink = "http://www.google.com/calendar/event?action=TEMPLATE"
                + "&text=" + encodeURIComponent(data['summary'])
                + "&dates=" + data['dtstart'] + "/" + data['dtend']
                + (data['location'] != null ? "&location=" + encodeURIComponent(data['location']) : "");
            
            return {'intent-action': 'ACTION_VIEW',
                    'intent-url': addLink,
                    'description-short': 'Add "' + eventSummary + '" to my calendar',
                    'description-long': 'Add "' + eventSummary + '" to my Google calendar'};
        }
        

        return null;
    }
    
    return action;
})();

