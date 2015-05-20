<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

	

	<body>
	 	<%@include file = "header.jsp"%>
		
		<script>
		var isConnected = false;
    $(function() {
    	
    	/* $.ajax({
            type: "POST",
            url: "getWifiSignal.do",
            data: ({}),
            cache: false,
            dataType: "text",
            success: onWifiSignalSuccess
        }); */
    	
    	
    	$('#id_host_ip').ipAddress();
        $("#id_btn_connectToServer").click(function() {
        	if(isConnected) return;
        	isConnected = true;
            var hostIP = $.trim($("#id_host_ip").val());
            var threshold = $("#id_turn_on_threshold").val(); 
            if(hostIP.length > 0)
            {
                $.ajax({
                  type: "POST",
                  url: "connectToServer.do",
                  data: ({hostIP: hostIP, threshold:threshold}),
                  cache: false,
                  dataType: "text",
                  success: onConnectSuccess
                });
            }
        });
        $("#id_btn_disconnectToServer").click(function() {
        	if(!isConnected) return;
        	isConnected = false;
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
        
        function onWifiSignalSuccess(data){
        	//alert(data);
        	$("#id_wifi_signal").text(data);
        	
        	$.ajax({
                type: "POST",
                url: "getWifiSignal.do",
                data: ({}),
                cache: false,
                dataType: "text",
                success: onWifiSignalSuccess
            });
        }
 
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
		
			<p>Current IP : ${myIP}</p>
			<p>WIFI Signal : <span id="id_wifi_signal">${wifi_signal}</span></p>
        	<div data-role="fieldcontain">
	         <label for="name"">Host IP Address (For Listening): </label>
	         <input type="text" name="name" id="id_host_ip" value=""  />
	         <label for="name"">Turn on signal threshold: </label>
	         <input type="range" name="slider-1" id="id_turn_on_threshold" value="-45" min="-100" max="0" />
	         <a href="#" data-role="button" data-icon="star" id="id_btn_connectToServer">Connect</a>
	         <a href="#" data-role="button" data-icon="star" id="id_btn_disconnectToServer" style="display:none">Disconnect</a>
			</div>

		<%@include file = "footer.jsp"%>

			
	</body>
</html>
