<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>

<!-- 

HTML code based on the Stlylish Portfolio by Start Bootstrap:

  http://startbootstrap.com/template-overviews/stylish-portfolio/

-->

<html>
<head>
	<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Bienvenido a qÜestiona</title>

    <!-- Bootstrap Core CSS -->
    <link href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="./css/stylish-portfolio.css" rel="stylesheet">

    <!-- Custom Fonts -->
    <link href="http://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,700,300italic,400italic,700italic" rel="stylesheet" type="text/css">

</head>

<body>

    <!-- Header -->
    <header id="top" class="header">
        <div class="text-vertical-center">
            <h1>qÜestiona</h1>
            <h3>Crea cuestionarios en segundos</h3>
            <br>
            <a href="<%= request.getAttribute("login") %>" class="btn btn-dark btn-lg">Accede con tu cuenta de Google</a>
        </div>
    </header>

    <!-- jQuery -->
    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>

</body>

</html>