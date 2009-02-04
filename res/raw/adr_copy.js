
(function() {
	
	var AdrCopy = function() { };

	AdrCopy.prototype.process = function(data)
	{
        if (data['street-address'] != null && (data['locality'] != null || data['postal-code'] != null)) {
            var fullAddr = data['street-address']
                    + (data['locality'] != null ? ", " + data['locality'] : "")
                    + (data['postal-code'] != null ? ", " + data['postal-code'] : "");
            
            return {'intent-action': 'TEXT_COPY',
            		'intent-url': fullAddr,
            		'icon': 'copy',
            		'description-short': 'Copy address to clipboard',
            		'description-long': 'Copy "' + data['street-address'] + '" to clipboard'};
        }
		
		return null;
	}
	
	return new AdrCopy();
})();
