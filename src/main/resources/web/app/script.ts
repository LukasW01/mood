import "bootstrap";
import htmx from "htmx.org";
import Alpine from 'alpinejs';

window.Alpine = Alpine
Alpine.start()

htmx.defineExtension('json-enc', {
    onEvent: function (name, evt): void {
        if (name === "htmx:configRequest") {
            evt.detail.headers['Content-Type'] = "application/json";
        }
    },

    encodeParameters : function(xhr, parameters, elt): string {
        xhr.overrideMimeType('text/json');
        return (JSON.stringify(parameters));
    }
});
