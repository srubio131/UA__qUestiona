package dai.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class NuevaPregunta extends HttpServlet {
	
	private Key existe_pregunta_para_tema(String tema, String pregunta, DatastoreService datastore, Key userKey)
	{
		Key preguntaKey = null;
	    Filter existe_tema = new FilterPredicate("tema", FilterOperator.EQUAL, tema);
	    Filter existe_pregunta = new FilterPredicate("pregunta", FilterOperator.EQUAL, pregunta);
	    Filter filtro = CompositeFilterOperator.and(existe_tema, existe_pregunta);
	    Query q = new Query("Pregunta").setAncestor(userKey).setFilter(filtro);
	    PreparedQuery pq = datastore.prepare(q);
	    
		if (pq.asSingleEntity() != null)
			preguntaKey = pq.asSingleEntity().getKey();
		
		return preguntaKey;
	}
	
	private JSONArray get_preguntas_dado_un_tema(String tema, DatastoreService datastore, Key userKey) throws JSONException
	{
		JSONArray preguntas = new JSONArray();
		JSONObject pregunta = new JSONObject();
		Filter existe_tema = new FilterPredicate("tema", FilterOperator.EQUAL, tema);
	    Query q = new Query("Pregunta").setAncestor(userKey).setFilter(existe_tema);
	    PreparedQuery pq = datastore.prepare(q);
		
	    for (Entity result : pq.asIterable()) {
	    	pregunta.put("pregunta", result.getProperty("pregunta").toString());
	    	pregunta.put("respuesta", result.getProperty("respuesta").toString());
	    	pregunta.put("tema", result.getProperty("tema").toString());
	    	preguntas.put(pregunta);
	    }	    
		
		return preguntas;
	}
	
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
    	    	
		String userName = request.getUserPrincipal().getName();
	    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Key userKey = KeyFactory.createKey("Usuario", userName);   	
				
		String pregunta = request.getParameter("pregunta");
		String respuesta = request.getParameter("respuesta");
		String tema = request.getParameter("tema");
		JSONObject json = new JSONObject();
		
		try {
			
			Key preguntaKey = existe_pregunta_para_tema(tema, pregunta, datastore, userKey);
			
	    	if (pregunta.isEmpty())
	    		json.put("error", new JSONObject().put("message", "se esperaba el parámetro pregunta"));
	    	if (respuesta.isEmpty())
	    		json.put("error", new JSONObject().put("message", "se esperaba el parámetro respuesta"));
	    	if (tema.isEmpty())
	    		json.put("error", new JSONObject().put("message", "se esperaba el parámetro tema"));
	    	else if (preguntaKey != null)
	    		json.put("error", new JSONObject().put("message", "la pregunta " + pregunta + " para el tema " +
	    												tema + " ya existe en la base de datos"));
	    	else
	    	{
	    		Entity ent_pregunta = new Entity("Pregunta", userKey);	    		 
	    		ent_pregunta.setProperty("pregunta", pregunta);	
	    		ent_pregunta.setProperty("respuesta", respuesta);
	    		ent_pregunta.setProperty("tema", tema);	
	    		datastore.put(ent_pregunta);
	    		
	    		Key key_pregunta = existe_pregunta_para_tema(tema, pregunta, datastore, userKey);
	    		if (key_pregunta != null)
	    			json.put("result", "[]");
	    		else
	    			json.put("error", new JSONObject().put("message", "la pregunta NO ha podido ser añadida. Pruebe de nuevo"));
	    	}
	    	
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().print(json);
    }
}
