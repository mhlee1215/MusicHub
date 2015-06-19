<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>

	<body>
	 	<%@include file = "header.jsp"%>
		
		<script>
		var isStart = false;
		
		<c:choose>
	      <c:when test="${isPlay==true}">
	      isStart = true;
	      </c:when>
	
	      <c:otherwise>
	      isStart = false;
	      </c:otherwise>
	    </c:choose>
		
    $(function() {
         $("#id_btn_startServer").click(function() {
        	 if(isStart == false){
        		isStart = true;
        		 
        		var threshold = $("#id_turn_on_threshold").val(); 
        		var islazy = $("#id_islazy").val(); 
        		var lazyNum = $("#id_lazy_num").val(); 
        		/* alert(islazy);
        		alert(lazyNum); */
        		"id_islazy"
                $.ajax({
                  type: "POST",
                  url: "startServer.do",
                  data: ({isLazy:islazy, lazyNum:lazyNum, threshold:threshold}),
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
        
        $('#id_islazy').change(function() {
        	var islazy = $("#id_islazy").val(); 
       		if(islazy == "off"){
       			$("#id_lazy_ppl_field").hide();
       		}else if(islazy == "on"){
       			$("#id_lazy_ppl_field").show();
       		}
        });
        
        /* $('#id_isLazy').change(function() {
        	  alert($('#id_isLazy').val());
        	}); */
 
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
            
	        
            
            <label for="name"">Turn on signal threshold: </label>
	         <input type="range" name="slider-1" id="id_turn_on_threshold" value="${threshold}" min="-100" max="0" />
            <div data-role="fieldcontain">
				<label for="slider2">Lazy Play:</label>
				<select name="slider2" id="id_islazy" data-role="slider">
					<option value="off" ${isLazy == "off" ? "selected=\"selected\"" : "" }>Off</option>
					<option value="on" ${isLazy == "on" ? "selected=\"selected\"" : "" }>On</option>
				</select>
			</div>
			<div data-role="fieldcontain" id="id_lazy_ppl_field" ${isLazy == "on" ? "" : "style=\"display:none\"" }>
			<label for="slider2">Lazy ppl num:</label>
			<input type="text" name="name" id="id_lazy_num" value="2"  />
			</div>
			
			<c:choose>
		      <c:when test="${isPlay==true}">
		      <a id="id_btn_stopServer" href="#" data-role="button" data-icon="star" data-theme="a">Stop Broadcasting</a>
		      <a href="#" data-role="button" style="display:none" data-icon="star" id="id_btn_startServer" data-theme="a">Broadcasting</a>
		      </c:when>
		
		      <c:otherwise>
		      <a id="id_btn_stopServer" style="display:none" href="#" data-role="button" data-icon="star" data-theme="a">Stop Broadcasting</a>
		      <a href="#" data-role="button" data-icon="star" id="id_btn_startServer" data-theme="a">Broadcasting</a>
		      </c:otherwise>
		    </c:choose>
		    
		    
			<br>
			
			<p>Connected Clients</p>
			<ul data-role="listview" data-count-theme="b" data-filter="true" data-inset="true">
			<c:forEach var="client" items="${clients}" varStatus="status">
				<li><a href="#">
			    	<img src="AudioLevelSpectrum.gif">
			    	<h2>${client.name}</h2>
			    	<p>${client.sockets }</p> 
			    <span class="ui-li-count">${client.signalStr}</span></a></li>
			</c:forEach>
			</ul>
			
			

		<%@include file = "footer.jsp"%>

			
	</body>
</html>
