<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Music Hub Configuration</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.css">
    <script src="https://code.jquery.com/jquery-1.8.2.min.js"></script>
    <script src="https://code.jquery.com/mobile/1.2.0/jquery.mobile-1.2.0.min.js"></script>
    <script src="js/jquery.input-ip-address-control-1.0.min.js"></script>
    
    <script>
    $(function() {
    	
    	$('#id_host_ip').ipAddress();
        $("#id_btn_connectToServer").click(function() {
            var hostIP = $.trim($("#id_host_ip").val());
            if(hostIP.length > 0)
            {
                $.ajax({
                  type: "POST",
                  url: "connectToServer.do",
                  data: ({hostIP: hostIP}),
                  cache: false,
                  dataType: "text",
                  success: onConnectSuccess
                });
            }
        });
        $("#id_btn_disconnectToServer").click(function() {
			$.ajax({
			  type: "POST",
			  url: "disconnectToServer.do",
			  cache: false,
			  dataType: "text",
			  success: onDisconnectSuccess
			});
        });
        /*
        $("#resultLog").ajaxError(function(event, request, settings, exception) {
          $("#resultLog").html("Error Calling: " + settings.url + "<br />HTTP Code: " + request.status);
        });
        */
 
        function onConnectSuccess(data)
        {
        	if (data == 'success'){
        		$("#id_btn_connectToServer").hide();
            	$("#id_btn_disconnectToServer").show();	
        	}
        }
        
        function onDisconnectSuccess(data)
        {
        	if (data == 'success'){
        		$("#id_btn_connectToServer").show();
            	$("#id_btn_disconnectToServer").hide();	
        	}
        }
 
    });
	</script>
    
</head>
<body>
    <div data-role="page">
 
        <div data-role="header">
            <h1>Music Hub Configuration</h1>
        </div><!-- /header -->
 
        <div data-role="content">
        	<p>Current IP : ${myIP}</p>
        	<div data-role="fieldcontain">
	         <label for="name"">Host IP Address (For Listening): </label>
	         <input type="text" name="name" id="id_host_ip" value=""  />
	         <a href="#" data-role="button" data-icon="star" id="id_btn_connectToServer">Connect</a>
	         <a href="#" data-role="button" data-icon="star" id="id_btn_disconnectToServer" style="display:none">Disconnect</a>
			</div>
        
        
            
            <p>Broadcasting</p>
            <input type="file"></input>
            <a href="#" data-role="button" data-icon="star" data-theme="a">Broadcasting</a>
            
            <div data-role="fieldcontain">
				<label for="slider2">Lazy Play:</label>
				<select name="slider2" id="slider2" data-role="slider">
					<option value="off">Off</option>
					<option value="on">On</option>
				</select>
			</div>
			
			<ul data-role="listview" data-count-theme="b" data-filter="true" data-inset="true">
			    <li><a href="#">
			    	<img src="AudioLevelSpectrum.gif">
			    	<h2>Player1</h2>
			    	<p>I am test player!</p> 
			    <span class="ui-li-count">12</span></a></li>
			    <li><a href="#">
			    	<img src="AudioLevelSpectrum.gif">
			    	<h2>Player2</h2>
			    	<p>I am another test player!</p> 
			    <span class="ui-li-count">0</span></a></li>
			    <li><a href="#">
			    	<img src="AudioLevelSpectrum.gif">
			    	<h2>Player3</h2>
			    	<p>I am third test player!</p>  
			    <span class="ui-li-count">4</span></a></li>
			    <li><a href="#">
			    	<img src="AudioLevelSpectrum.gif">
			    	<h2>Player4</h2>
			    	<p>I am hidden test player!</p>  
			    <span class="ui-li-count">328</span></a></li>
			    <li><a href="#">
			    	<img src="AudioLevelSpectrum.gif">
			    	<h2>Player5</h2>
			    	<p>I am the last test player!</p> 
			    <span class="ui-li-count">62</span></a></li>
			</ul>
						
      
        </div><!-- /content -->
 
        <div data-role="footer"> 
            <h4>Music Hub Development Team</h4>
        </div><!-- /footer -->
 
    </div><!-- /page -->1
</body>
</html>