
(function() {
    
    var AddressToGmap = function() { };

    AddressToGmap.prototype.process = function(data)
    {
        if (data['street-address'] != null && (data['locality'] != null || data['postal-code'] != null)) {
            var fullAddr = data['street-address']
                    + (data['locality'] != null ? ", " + data['locality'] : "")
                    + (data['postal-code'] != null ? ", " + data['postal-code'] : "");
            
            return {'intent-action': 'ACTION_VIEW',
                    'intent-url': "geo:0,0?q=" + encodeURIComponent(fullAddr),
                    'icon': 'com.lexandera.scripts.adr_to_gmap',
                    'description-short': 'Show "' + data['street-address'] + '" on map',
                    'description-long': 'Show "' + data['street-address'] + '" using maps application'};
        }
        
        return null;
    }
    
    return new AddressToGmap();
})();
