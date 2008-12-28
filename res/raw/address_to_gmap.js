
/* Locates 'adr' elements and extracts location information from them, which it then passes to AddressToGmapInterface */
(function() {
    var addrs = getElementsByAttribute(document.body, '*', 'class', 'adr');
    
    for (var i = 0; i < addrs.length; i++) {
    	var addr = addrs[i];
    	
    	var streets = getElementsByAttribute(addr, '*', 'class', 'street-address');
    	var localities = getElementsByAttribute(addr, '*', 'class', 'locality');
    	var postalCodes = getElementsByAttribute(addr, '*', 'class', 'postal-code');
    	
    	var appendHTML;
    	if (appendHTML = window.AddressToGmapInterface.addAddress(streets[0].innerHTML, localities[0].innerHTML, postalCodes[0].innerHTML)) {
    		/* rewrite contents - append action link */
    		addr.innerHTML = addr.innerHTML + appendHTML;
    	}
    	
    }
})();
