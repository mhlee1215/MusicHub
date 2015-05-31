<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>

	

	<body>
	 	<%@include file = "header.jsp"%>
		
		<script>
		var isConnected = false;
		
		<c:choose>
	      <c:when test="${isPlay==true}">
	      isConnected = true;
	      </c:when>
	
	      <c:otherwise>
	      isConnected = false;
	      </c:otherwise>
	    </c:choose>
		
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
        	var clientName = $.trim($("#id_name").val());
            var hostIP = $.trim($("#id_host_ip").val());
            var threshold = $("#id_turn_on_threshold").val(); 
            if(hostIP.length > 0)
            {
                $.ajax({
                  type: "POST",
                  url: "connectToServer.do",
                  data: ({hostIP: hostIP, threshold:threshold, clientName:clientName}),
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
		<div data-role="fieldcontain">
			<label for="name">Name: </label>
	        <input type="text" name="name" id="id_name" value="${name}"  />
			<p>Current IP : ${myIP}</p>
			<p>WIFI Signal : <span id="id_wifi_signal">${wifi_signal}</span></p>
        	
	         <label for="name"">Host IP Address (For Listening): </label>
	         <input type="text" name="name" id="id_host_ip" value="${ip}"  />
	         <label for="name"">Turn on signal threshold: </label>
	         <input type="range" name="slider-1" id="id_turn_on_threshold" value="-45" min="-100" max="0" />
	         
	         <c:choose>
		      <c:when test="${isPlay==true}">
		     <a href="#" data-role="button" data-icon="star" id="id_btn_connectToServer" style="display:none" data-theme="a">Connect</a>
	         <a href="#" data-role="button" data-icon="star" id="id_btn_disconnectToServer" data-theme="a">Disconnect</a>
		      </c:when>
		
		      <c:otherwise>		  
		      <a href="#" data-role="button" data-icon="star" id="id_btn_connectToServer" data-theme="a">Connect</a>
	          <a href="#" data-role="button" data-icon="star" id="id_btn_disconnectToServer" style="display:none" data-theme="a">Disconnect</a>
		      </c:otherwise>
		    </c:choose>
		    
	         
			</div>

		<%@include file = "footer.jsp"%>

			
	</body>
</html>
