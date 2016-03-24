<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">

	<link href='http://fonts.googleapis.com/css?family=PT+Sans:400,700' rel='stylesheet' type='text/css'>

	<link rel="stylesheet" href="css/reset.css"> <!-- CSS reset -->
	<link rel="stylesheet" href="css/style.css"> <!-- Gem style -->
	<link rel="stylesheet" href="css/more.css"> <!-- more style -->
	
	
	<script src="js/modernizr.js"></script> <!-- Modernizr -->
  	
	<title>Log In &amp; Sign Up Form</title>
</head>

	<header role="banner">
		<div id="cd-logo"><img src="images/cd-logo.svg" alt="Logo"></div>
		
			<c:choose>
		      <c:when test="${loginComplete==true}">
				<nav class="main-nav">		
					<ul>
						<!-- inser more links here -->
						<li><img src="images/icon_brain.png" height="26px" width="26px"></li>
	
						<li><p>Hello, ${loginName}!</p></li>
						<li><a href = "login.do">(sign out)</a></li>
						 
						<!-- <li><a class="cd-signup" href="#0">Sign up</a></li> -->
					</ul>
				</nav>
				      		      
		      </c:when>
		
		      <c:otherwise> 
				<nav class="main-nav">
					<ul>
						<!-- inser more links here -->
						<li><a class="cd-signin" href="#0">Sign in</a></li>
						<li><a class="cd-signup" href="#0">Sign up</a></li>
					</ul>
				</nav>
				      	 
		      </c:otherwise>
		    </c:choose>
		    
		
	</header>
	
	<section id="search" class="container">
	<div class="container-inner">
		<div class="twelvecol">
			<p>Search for a channel </p> 
			<div class="search">
				<form action="" method="get" id="searchform">
				    <fieldset>
				        <input type="text" name="filter" id="filter" placeholder="Ex. Jazz ">
			             <span id="clear">x</span>
				    </fieldset>
				</form>
			</div>		
		</div>
	</div>
</section>
	
	
	