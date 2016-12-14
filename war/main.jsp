<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!doctype html>
<html>
<head>
	<meta charset="UTF-8">
    <title>qÜestiona</title>
    <link id="estilo" rel="stylesheet" type="text/css" href="./css/normal.css">
    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
    <script type="text/javascript" src="./components/webcomponentsjs/webcomponents.js"></script>
    <script type="text/javascript" src="./js/cuestionario.js"></script>
    
    <link rel="import" href="./miscomponentes/encabezado-cuestionario.html">
    
         
</head>

<body>
    <header>
        <h1>qÜestiona</h1>
        <p>qÜestiona es una aplicación web que permite gestionar cuestionarios sobre un determinado tema en el que las posibles respuestas son verdadero o falso. La aplicación se desarrolla durante el curso 2014-2015 como parte de las prácticas de la asignatura Desarrollo de Aplicaciones en Internet del Grado en Ingeniería Informática de la Universitat d'Alacant</p>
        <nav>
            <ul></ul>
        </nav>
    </header>
    <main>        
        <div class="formulario" id="nuevoCuestionario">
            <ul>
               <li>
                   <label>Tema del cuestionario:</label>
                   <input type="text" name="tema" autofocus>
               </li>
               <li>
                   <input type="button" name="crea" value="Crear nuevo cuestionario">
               </li>
            </ul>
        </div>
    </main>
    <footer>
        <span id="nombre">Sergio Rubio Anaya</span> - <span id="dni">48674755Q</span> | Práctica 4 de Desarrollo de Aplicaciones en Internet | Universitat d'Alacant | <span id="usuario"><%= request.getAttribute("usuario") %></span> | <span id="logout"><a href="<%= request.getAttribute("logout") %>">Salir</a></span>
    </footer>
</body>
</html>