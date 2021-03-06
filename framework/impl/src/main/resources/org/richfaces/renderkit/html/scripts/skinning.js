{
	var mediaName = "rich-extended-skinning";
	
	var userAgent = navigator.userAgent;
	
	var skipNavigator = window.opera || (userAgent.indexOf('AppleWebKit/') > -1 && userAgent.indexOf('Chrome/') == -1);
	if (!skipNavigator) {

		var resetMedia = function(elt) {
			var media = elt.getAttribute('media');
			
			if (mediaName == media) {
				elt.removeAttribute('media');
			}
		};
		
		if (!window._RICH_FACES_SKINNING_ADDED_TO_BODY) {
			var getElementByTagName = function(elt, name) {
		   		var elements; 
			    try {
			        elements = elt.selectNodes(".//*[local-name()=\"" + 
			                                           name + "\"]");
			    } catch (ex) {
			    	try {
						elements = elt.getElementsByTagName(name);
			    	} catch (nf) {
						//ok, give up, no elements found
			    	}
			    }
		
				return elements;
			};
		
			var f = function() {
				if (window.RICH_FACES_EXTENDED_SKINNING_ON) {
					var styles = getElementByTagName(document, 'link');
					if (styles) {
						var l = styles.length;
						for (var i = 0; i < l; i++) {
							var elt = styles[i];
							resetMedia(elt);
						}
					}
				}				
			};

			if (window.addEventListener) {
				window.addEventListener("load", f, false);
			} else {
				window.attachEvent("onload", f);
			}

			window._RICH_FACES_SKINNING_ADDED_TO_BODY = true;
		}
		
		if (!window._RICH_FACES_SKINNING_ADDED_TO_AJAX && typeof A4J != "undefined" && A4J.AJAX) {
			A4J.AJAX.AddHeadElementTransformer(function (elt) {
				if (window.RICH_FACES_EXTENDED_SKINNING_ON) {
					if (elt.tagName && elt.tagName.toLowerCase() == 'link') {
						resetMedia(elt);
					}
				}
			});
			window._RICH_FACES_SKINNING_ADDED_TO_AJAX = true;
		}
	}
};
