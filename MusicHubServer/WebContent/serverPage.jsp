<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

	<body>
	 	<%@include file = "header.jsp"%>
		
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

		<%@include file = "footer.jsp"%>

			
	</body>
</html>
