/**** AUXILIARES ****/
// Limpiar valores introducidos en los inputs
function limpiarInputs(nodo) {
    var inputs_text = nodo.querySelectorAll('input[type=text]');
    var inputs_radio = nodo.querySelectorAll('input[value=verdadero]');
    
    for(var i=0; i<inputs_text.length ;i++) {
        inputs_text[i].value = "";
    }
    
    for(var i=0; i<inputs_radio.length ;i++) {
        inputs_radio[i].checked = true;
    }
}

// Insertar nuevo hijo al final del nodo padre
function insertAsLastChild(padre, nuevoHijo) {    
    padre.appendChild(nuevoHijo);
}

// Insertar nuevo hijo al principio de todos los hijos del nodo padre
function insertAsFirstChild(padre, nuevoHijo) {    
    if(padre.hasChildNodes()) {
        var primer_hijo = padre.childNodes[0];
        padre.insertBefore(nuevoHijo, primer_hijo);
    } else {
        insertAsLastChild(nuevoHijo);
    }
}

// Insertar nuevo hijo en nodo padre antes del hijo especificado
function insertBeforeChild(padre, hijo, nuevoHijo) {    
    var hijos = padre.childNodes;
    var tam_hijos = hijos.length;
    
    if(hijos[0] === hijo) {
        insertAsFirstChild(padre, nuevoHijo);
    } else if(hijos[tam_hijos-1] === hijo) {
        insertAsLastChild(padre, nuevoHijo);
    } else {
        padre.insertBefore(nuevoHijo, hijo);
    }
}

// Eliminar Nodo del DOM
function removeElement(nodo) {
    nodo.parentNode.removeChild(nodo);
}

// Selecciona el ancestro de un nodo
function queryAncestorSelector (node, selector) {    
    var parent = node.parentNode;
    var all = document.querySelectorAll(selector);
    var found = false;
    
    while (parent !== document && !found) {
        for (var i = 0; i < all.length && !found; i++) {
            found = (all[i] === parent)?true:false;
        }
        parent = (!found)?parent.parentNode:parent;
    }
    
    return (found)?parent:null;
}

/** Funciones para el manejo de los cuestionarios **/
// Inserta una cruz en una pregunta y crea un evento para poder eliminarla
function addCruz(nodo_bloque) {
    var nuevoDiv = document.createElement("div");
    var nodoSimboloUnicode = document.createTextNode("\u2612");
    nuevoDiv.setAttribute('class','borra');
    nuevoDiv.appendChild(nodoSimboloUnicode);
    
    // Registro manejador de eventos
    nuevoDiv.addEventListener("click", borraPregunta, false);
    insertAsFirstChild(nodo_bloque, nuevoDiv);
}

function borraPregunta(event) {
    var nodo_actual = event.target;
    var bloque_a_borrar = queryAncestorSelector(nodo_actual, '.bloque');
    var cuestionario = queryAncestorSelector(nodo_actual, 'section');
    var encabezado_cuestionario = cuestionario.querySelector('encabezado-cuestionario');
    var div_pregunta = bloque_a_borrar.querySelector('div[class=pregunta]');
    
    // Consultar si borrar cuestionario
    $.getJSON('/borrapregunta?tema=' + encabezado_cuestionario.tema + '&pregunta=' + div_pregunta.textContent, function(response){
		
        if(response.result) {
        	removeElement(bloque_a_borrar);
        	
        	if(cuestionario.childElementCount === 2) { // encabezado-cuestionario y form
        	    // Consultar si borrar cuestionario
        	    $.getJSON('/borracuestionario?tema='+encabezado_cuestionario.tema, function(response){			
        	        if(response.result) 
        	        {
        	        	removeElement(cuestionario);
        	        	var div_a = document.querySelector('nav').querySelector('a[href*=' + cuestionario.getAttribute('id') + ']');
        	        	var div_li = queryAncestorSelector(div_a, 'li');
        	            removeElement(div_li);
        	        
        	        } else {
        	        	alert(response.error.message);
        	        }
        	    });
            }
        }
        else
        	alert(response.error.message);
    });
}

function addFormPregunta(section) {  
    var form = document.createElement("div");
    form.setAttribute('class', 'formulario');
    var ul = document.createElement("ul");
    var li1 = document.createElement("li");
    var label1 = document.createElement("label");
    label1.appendChild(document.createTextNode("Enunciado de la pregunta:"));
    var input1 = document.createElement('input');
    input1.setAttribute('type','text');
    input1.setAttribute('name',section.id+"_pregunta");
    
    var li2 = document.createElement("li");
    var label2 = document.createElement("label");
    label2.appendChild(document.createTextNode("Respuesta:"));
    var input2 = document.createElement('input');
    input2.setAttribute('type','radio');
    input2.setAttribute('name',section.id+"_respuesta");
    input2.setAttribute('value','verdadero');
    input2.setAttribute('checked','');    
    var input3 = document.createElement('input');
    input3.setAttribute('type','radio');
    input3.setAttribute('name',section.id+"_respuesta");
    input3.setAttribute('value','falso');
    
    var li3 = document.createElement("li");
    var input4 = document.createElement('input');
    input4.setAttribute('type','button');
    input4.setAttribute('value','Añadir nueva pregunta');
    
    li1.appendChild(label1);
    li1.appendChild(input1);
    li2.appendChild(label2);
    li2.appendChild(input2);
    li2.appendChild(document.createTextNode('Verdadero'));
    li2.appendChild(input3);
    li2.appendChild(document.createTextNode('Falso'));
    li3.appendChild(input4);
    
    ul.appendChild(li1);
    ul.appendChild(li2);
    ul.appendChild(li3);
    
    form.appendChild(ul);
    
    // Registro manejador de eventos
    input4.addEventListener("click", addPregunta, true);
    
    var divs = section.querySelectorAll('div');
    if(divs.length > 0) {
        insertBeforeChild(section, divs[0], form);
    } else {
        insertAsLastChild(section, form);
    }
    
    return form;
}

function creaBloquePregunta(pregunta, respuesta) {
	
	var bloque_pregunta = document.createElement("div");
    bloque_pregunta.className = 'bloque';
    var div_pregunta = document.createElement("div");
    div_pregunta.className = 'pregunta';
    div_pregunta.appendChild(document.createTextNode(pregunta));
    
    var div_respuesta = document.createElement("div");
    div_respuesta.className = 'respuesta';
    div_respuesta.setAttribute('data-valor', respuesta);

    bloque_pregunta.appendChild(div_pregunta);
    bloque_pregunta.appendChild(div_respuesta);
    
    // Añadir cruz
	addCruz(bloque_pregunta);

    return bloque_pregunta;
}

function addPregunta(event) {    
    var nodo_actual = queryAncestorSelector(event.target, '.formulario');    
    var input_enunciado = nodo_actual.querySelector('input[type=text]');
    var input_respuesta = nodo_actual.querySelector('input[type=radio]');
    var cuestionario = queryAncestorSelector(nodo_actual, 'section');
    var encabezado_cuestionario = cuestionario.querySelector('encabezado-cuestionario');
    
    if(input_enunciado.value === "") {
        alert("Error: Rellena todos los campos");
    } else { // Intentamos crear la pregunta
    	
    	var pregunta = input_enunciado.value;
    	var respuesta = input_respuesta.checked;
    	
    	$.getJSON('/nuevapregunta?tema=' + encabezado_cuestionario.tema + '&pregunta=' + pregunta
    				+ '&respuesta=' + respuesta, function(response){    	
    		
            if(response.result){
            	var bloque_pregunta = creaBloquePregunta(pregunta, respuesta);
            	// Añadir al final la pregunta
				insertAsLastChild(cuestionario, bloque_pregunta);
            }
            else
            	alert(response.error.message);          
        });
    }
    // Limpiar Input
    limpiarInputs(nodo_actual);
}

function addEncabezadosYFormulariosPreguntas(main, tema) {
	
	var seccion = document.createElement('section');
    seccion.setAttribute('id', "c"+num_cuestionario);
    
	// Añadir encabezado-cuestionario
    var encabezado = document.createElement('encabezado-cuestionario');
    encabezado.setAttribute('tema', tema);
    insertAsLastChild(seccion, encabezado);
    // Añadir formulario pregunta
    addFormPregunta(seccion);        
    insertAsLastChild(main, seccion);
    
    // Añadir link
    var nav_ul = document.querySelector('nav').querySelector('ul');
    var li = document.createElement('li');
    var a = document.createElement('a');
    a.setAttribute('href','#'+seccion.id);
    a.appendChild(document.createTextNode(tema));
    
    li.appendChild(a);
    nav_ul.appendChild(li);
    insertAsLastChild(nav_ul.parentNode, nav_ul);
    
    num_cuestionario++;    
}

function addCuestionario(event) {
    var form_cuestionario = queryAncestorSelector(event.target, '.formulario');
    var main = queryAncestorSelector(form_cuestionario, 'main');
    var input_tema = form_cuestionario.querySelector('input[name=tema]');
    
    if(input_tema.value === "") {
        alert("Error: Rellena todos los campos");        
    } else { // Intentamos crear el nuevo cuestionario  
    
	    $.getJSON('/nuevocuestionario?tema='+ input_tema.value, function(response){
	    	
	    	if(response.result) 
	        {
	    		addEncabezadosYFormulariosPreguntas(main, input_tema.value);	    		
	    		
	    		// Limpiar formulario
	            limpiarInputs(form_cuestionario);
	        
	        } else {
	        	alert(response.error.message);
	        }
	    });
    }
}

/**** FUNCIONES PARA INICIO ****/

window.onload = function() {
	
	// Variable global
	num_cuestionario=1;
	
	$.getJSON('/listacuestionarios', function(response){
		
        if(response.result) 
        {
            var array_temas = response.result;
            var main = document.querySelector('main');
            
            $.each(array_temas, function(index1, item1) {
            	            
            	var tema = item1;
            	addEncabezadosYFormulariosPreguntas(main, tema);
            	
            	$.getJSON('/listapreguntas?tema=' + tema, function(response){
            		
                    if(response.result) 
                    {
                        var array_preguntas = response.result;
                        var encabezado_cuestionario = document.querySelector("encabezado-cuestionario[tema='" + tema + "']");
                        var cuestionario = queryAncestorSelector(encabezado_cuestionario, 'section');
                        
                        $.each(array_preguntas, function(index2, item2) {
                        	
                        	var bloque_pregunta = creaBloquePregunta(item2.pregunta,item2.respuesta);
                        	// Añadir al final la pregunta
            				insertAsLastChild(cuestionario, bloque_pregunta);            				
                        });
                        
                    } else {
                    	alert(response.error.message);
                    }        
                });            	
            });
            
        } else {
        	alert(response.error.message);
        }        
    });
	
    document.addEventListener("DOMContentLoaded", addCruz, false);
    // Registro manejador de eventos para el boton "Crear nuevo cuestionario"
    document.querySelector('input[name=crea]').addEventListener("click", addCuestionario, false);
};