(function(){
	
	var PROFILE_URL_PATTERN = /^https?:\/\/(?:www\.)?instagram\.com\/(.*)(?:\/|$)/i;
	
	var form = $('form');
	var usernameOrUrlEl = $('input[name=usernameOrUrl]');
	
	form.on('submit', function(event){
		event.preventDefault();
		
		var usernameOrUrl = usernameOrUrlEl.val();
		
		if(usernameOrUrl.match){
			var urlMatches = usernameOrUrl.match(PROFILE_URL_PATTERN);
			var username;
			
			if(urlMatches){
				username = urlMatches[1];
			} else {
				username = usernameOrUrl;
			}
			
			window.console.info("username = "+username);
			var path = window.location.pathname;
			path = path.replace(/index\.html$/, "");
			if(!/\/$/.test(path)){
				path += "/";
			}
			path += "api/users/"+window.encodeURIComponent(username)+"/rss";
			
			window.console.info("Going to "+path);
			window.location.pathname = path;
		}
	});
	
	
})();