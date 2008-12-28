
/* Locates site-wide search form and passes data about it to SiteSearchInterface. It then hides the HTML search form. */
(function() {
    var searchForms = getElementsByAttribute(document.body, 'form', 'class', 'site-search');
    if (searchForms.length > 0) {
        var action = searchForms[0].getAttribute('action');
        var searchFields = getElementsByAttribute(searchForms[0], 'input', 'class', 'site-search-query');
        var searchDescs = getElementsByAttribute(searchForms[0], '*', 'class', 'site-search-description');

        if (action && searchFields.length > 0 && searchDescs.length > 0) {
            window.SiteSearchInterface.setSearchFormData(
                action, 
                searchFields[0].getAttribute('name'), 
                searchDescs[0].innerHTML);
                
            searchForms[0].style.display = 'none';
        } 
    }
})();
