
/* Locates 'vevent' elements and extracts event information from them, which it then passes to EventToGcalInterface  */

(function() {
    var scripts;
    eval("scripts = " + window.ActionInterface.getScriptsFor('vevent'));
    
    if (scripts.length == 0) {
        return;
    }
    
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
        
        var microformatData = {'location': eventLocation,
                               'summary': eventSummary,
                               'dtstart': startDate,
                               'dtend': endDate};
        
        var showLink = false;
        var groupId = window.ActionInterface.startNewActionGroup();
        
        for (var x=0; x<scripts.length; x++) {
            var obj;
            eval("obj = " + scripts[x]);
            var actionData = obj.process(microformatData, event);
            
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
            var txt = microformatData['summary'];
            if (txt.length > 23) {
                txt = txt.substring(0, 20) + '...';
            }
            
            event.innerHTML += window.ActionInterface.actionGroupLink(groupId, 'Actions for "'+txt+'"');
        }
    }
})();
