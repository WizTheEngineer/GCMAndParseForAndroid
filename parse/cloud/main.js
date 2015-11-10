
// Send messages via Google Cloud Messenger
Parse.Cloud.define("sendMessage", function(request, response) {
	// Get the sender
	var sender = Parse.User.current();

	// Get the message
	var message = request.params.message;

	// Query for the receiving user by email address
	var receiverEmail = request.params.receiverEmail;

	var query = new Parse.Query(Parse.User);
	query.equalTo("email", receiverEmail);
	query.first({
	  success: function(receiver) {
	  	if (receiver) {

	  		// Send the message!
	  		sendGoogleCloudMessage(sender, message, receiver, response);
	  	} else {

	  		// Uh-oh! User not found.
	  		response.success("No user found by that email address");
	  	}
	  },
	  error: function(error) {
	    response.error("Error: " + error.code + " " + error.message);
	  }
	});
});

function sendGoogleCloudMessage(sender, message, receiver, response) {
	var GCM_API_KEY = "AIzaSyDhRdlsQH3VDe8P_0d-HmBXlXVvHIu4YRc";

	Parse.Cloud.httpRequest({
	  method: 'POST',
	  url: 'https://android.googleapis.com/gcm/send',
	  headers: {
	    'Authorization': 'key=' + GCM_API_KEY,
	    'Content-Type': 'application/json'
	  },
	  body: {
	    to: receiver.get("gcmRegistrationToken"),
	    data: {
	      "senderId" : sender.id,
	      "senderEmail" : sender.get("email"),
	      "message" : message,
	      "timeSent" : new Date()
	    }
	  },
	  success: function(httpResponse) {
	    // Do something
	    response.success("GCM Message success" + JSON.stringify(httpResponse));
	  },
	  error: function(httpResponse) {
	    response.error('GCM Request failed' + JSON.stringify(httpResponse));
	  }
	});
}
