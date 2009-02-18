// ==Action==
// @name           Copy an address to clipboard
// @id             com.lexandera.scripts.AddressCopyToClipboard
// @type           microformat
// @handles        adr
// ==/Action==

(function() {
    var action = function() { };
    action.id = 'com.lexandera.scripts.AddressCopyToClipboard';
    action.process = function(data, matchedNode)
    {
        if (data['street-address'] != null && (data['locality'] != null || data['postal-code'] != null)) {
            var fullAddr = data['street-address']
                    + (data['locality'] != null ? ", " + data['locality'] : "")
                    + (data['postal-code'] != null ? ", " + data['postal-code'] : "");
            
            return {'intent-action': 'TEXT_COPY',
                    'content': fullAddr,
                    'description-short': 'Copy address to clipboard',
                    'description-long': 'Copy "' + data['street-address'] + '" to clipboard'};
        }
        
        return null;
    }
    
    return action;
})();
