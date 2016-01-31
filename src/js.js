<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Chat Example Using STOMP Over WebSockets</title>
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/bootstrap.min.responsive.css" rel="stylesheet">
    <style type="text/css">
      body { padding-top: 40px; }
    </style>
  </head>

  <body>
    
    <div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="#">Era Insight Client</a>
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span6">
          <div id="connect">
            <div class="page-header">
              <h2>Server Login</h2>
            </div>
            <form class="form-horizontal" id='connect_form'>
              <fieldset>
                <div class="control-group">
                  <label>WebSocket URL</label>
                  <div class="controls">
                    <input name=url id='connect_url' value='ws://localhost:61614' type="text">
                  </div>
                </div>
                <div class="control-group">
                  <label>User</label>
                  <div class="controls">
                    <input id='connect_login' placeholder="User Login" value="admin" type="text">
                  </div>
                </div>
                <div class="control-group">
                  <label>Password</label>
                  <div class="controls">
                    <input id='connect_passcode' placeholder="User Password" value="password" type="password">
                  </div>
                </div>
                <div class="control-group">
                  <label>Destination</label>
                  <div class="controls">
                    <input id='destination' placeholder="Destination" value="/topic/chat.*" type="text">
                  </div>
                </div>
                <div class="form-actions">
                  <button id='connect_submit' type="submit" class="btn btn-large btn-primary">Connect</button>
                </div>
              </fieldset>
            </form>
          </div>
          <div id="connected" style="display:none">
            <div class="page-header">
              <h2>Chat Room</h2>
            </div>
            <div id="messages">
            </div>
            <form class="well form-search" id='send_form'>
              <button class="btn" type="button" id='disconnect' style="float:right">Disconnect</button>
              <input class="input-medium" id='send_form_input' placeholder="Type your message here" class="span6"/>
              <button class="btn" type="submit">Send</button>
            </form>
          </div>
        </div>
        <div class="span4">
          <div class="page-header">
            <h2>Debug Log</h2>
          </div>
          <pre id="debug"></pre>
        </div>
      </div>
    </div>

    <!-- Scripts placed at the end of the document so the pages load faster -->
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js"></script>
    <script src="/stomp.js"></script>
    <script>//<![CDATA[
    $(document).ready(function() {
	

	
      if(window.WebSocket) {
          
        var client, destination, newDestination;
          
        $('#connect_form').submit(function() {
        
          var url = $("#connect_url").val();
          var login = $("#connect_login").val();
          var passcode = $("#connect_passcode").val();
          destination = $("#destination").val();
            var connected = true;
          client = Stomp.client(url);

          // this allows to display debug logs directly on the web page
          client.debug = function(str) {
            $("#debug").append(str + "\n");
          };
          
          // the client is notified when it is connected to the server.
          client.connect(login, passcode, function(frame) {
		  console.log("#################################################"+frame.headers.session);
		  
		  console.log(" ****************Client***************");
		  console.debug(frame);
            client.debug("connected to Stomp");
            $('#connect').fadeOut({ duration: 'fast' });
            $('#connected').fadeIn();
			
              
              
//            var text = "Initilizing";
//			client.send(destination, {}, text);
//              
//			if(connected){
//               var subscription = client.subscribe(destination, function(message) {
//
//                console.log("#################################################"+frame.headers['message-id']);
//                 console.debug("message is ");
//                   console.debug(message);
//
//               //   $("#messages").append("<p>" + frame.headers.session + "</p>\n");
//                  $("#messages").append("<p>" + message.headers.destination + "</p>\n");
//                   $("#messages").append("<p>" + message.body + "</p>\n");
//                });
//                connected = false;
//                console.log("connected:  "+connected);
//                subscription.unsubscribe();
//                
//          }
              newDestination = frame.headers.session;
              newDestination = newDestination.replace(/:|-/g, '');
              console.log("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+newDestination);
              destination = destination.replace('*','');
              destination = destination +newDestination;
              
            console.log("changed destination:  " +destination);
              
               var subscription = client.subscribe(destination, function(message) {

                var text = readJSON(message.body);
				console.log("#################################################"+frame.headers['message-id']);
                 console.debug("message is ");
				 console.debug(message);

               //   $("#messages").append("<p>" + frame.headers.session + "</p>\n");
                  $("#messages").append("<p>" + message.headers.destination + "</p>\n");
                   //$("#messages").append("<p>" + message.body + "</p>\n");
				   $("#messages").append("<p>" + text + "</p>\n");
                });
              
              
          });
            
          return false;
        });
  
        $('#disconnect').click(function() {
          client.disconnect(function() {
            $('#connected').fadeOut({ duration: 'fast' });
            $('#connect').fadeIn();
            $("#messages").html("")
          });
          return false;
        });
   
        $('#send_form').submit(function() {
          var text = $('#send_form_input').val();
          if (text) {
			text = createJSON(text);
            client.send(destination, {}, text);
            $('#send_form_input').val("");
          }
          return false;
        });
		
		
	function readJSON(text){
		
		var obj = JSON.parse(text);
		return obj.text;
	}
	
	function createJSON(text){
	var str="{\"owner\":\"Mursith\",\"text\":\""+text+"\"}";
	return str;
	}
		
      } else {
        $("#connect").html("\
            <h1>Get a new Web Browser!</h1>\
            <p>\
            Your browser does not support WebSockets. This example will not work properly.<br>\
            Please use a Web Browser with WebSockets support (WebKit or Google Chrome).\
            </p>\
        ");
      }
    });
    //]]></script>

  </body>
</html>
