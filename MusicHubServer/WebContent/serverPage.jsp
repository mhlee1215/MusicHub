<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

	<body>
	 	<%@include file = "header.jsp"%>
		
		<script>
		var isStart = false;
    $(function() {
         $("#id_btn_startServer").click(function() {
        	 if(isStart == false){
        		 isStart = true;
                $.ajax({
                  type: "POST",
                  url: "startServer.do",
                  cache: false,
                  dataType: "text",
                  success: onStartSuccess
                });
        	 }

        });
        $("#id_btn_stopServer").click(function() {
        	if(isStart == true){
        		isStart = false;
				$.ajax({
				  type: "POST",
				  url: "stopServer.do",
				  cache: false,
				  dataType: "text",
				  success: onStopSuccess
				});
        	}
        });
        /*
        $("#resultLog").ajaxError(function(event, request, settings, exception) {
          $("#resultLog").html("Error Calling: " + settings.url + "<br />HTTP Code: " + request.status);
        });
        */
 
        function onStartSuccess(data)
        {
        	//alert(data);
        	if (data == 'success'){
        		$("#id_btn_startServer").hide();
            	$("#id_btn_stopServer").show();	
        	}
        }
        
        function onStopSuccess(data)
        {
        	if (data == 'success'){
        		
        		$("#id_btn_startServer").show();
            	$("#id_btn_stopServer").hide();	
        	}
        }
        
       
 
    });
    
    function blank(){
    	
    }
	</script>
		
				<p>Broadcasting</p>
            <input type="file"></input>
            
	         <a href="#" data-role="button" data-icon="star" id="id_btn_startServer" data-theme="a">Broadcasting</a>
            
            <a id="id_btn_stopServer" style="display:none" href="#" data-role="button" data-icon="star" data-theme="a">Stop Broadcasting</a>
            
            
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

		<%@include file = "footer.jsp"%>

			
	</body>
</html>
