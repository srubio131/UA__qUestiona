package dai.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class BorraPregunta extends HttpServlet {
	
	private Key existe_pregunta(String pregunta, String tema, DatastoreService datastore, Key userKey)
	{
		Key preguntaKey = null;
	    Filter existe_pregunta = new FilterPredicate("pregunta", FilterOperator.EQUAL, pregunta);
	    Filter existe_tema = new FilterPredicate("tema", FilterOperator.EQUAL, tema);
	    Filter filtro = CompositeFilterOperator.and(existe_pregunta, existe_tema);
	    Query q = new Query("Pregunta").setAncestor(userKey).setFilter(filtro);
	    PreparedQuery pq = datastore.prepare(q);
	    
		if (pq.asSingleEntity() != null)
			preguntaKey = pq.asSingleEntity().getKey();
		
		return preguntaKey;
	}
	
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
    	
    	String userName = request.getUserPrincipal().getName();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key userKey = KeyFactory.createKey("Usuario", userName);        
    	
    	String tema = request.getParameter("tema");
    	String pregunta = request.getParameter("pregunta");
    	JSONObject json = new JSONObject();
    	
    	try {
    		
    		Key preguntaKey = existe_pregunta(pregunta, tema, datastore, userKey);
    		
	    	if (tema.isEmpty())
	    		json.put("error", new JSONObject().put("message", "se esperaba el parámetro tema"));
	    	if (pregunta.isEmpty())
	    		json.put("error", new JSONObject().put("message", "se esperaba el parámetro pregunta"));
	    	else if (preguntaKey == null)
	    		json.put("error", new JSONObject().put("message", "la pregunta " + pregunta 
	    				+ " del tema " + tema + " No existe en la base de datos"));
	    	else
	    	{ 		
	    		datastore.delete(preguntaKey);
	    		
	    		Key key_pregunta = existe_pregunta(pregunta, tema, datastore, userKey);
	    		if (key_pregunta == null)
	    			json.put("result", "[]");
	    		else
	    			json.put("error", new JSONObject().put("message", "la pregunta NO ha podido ser borrada. Pruebe de nuevo"));
	    	}
	    	
    	} catch(JSONException e) {
    		e.printStackTrace();
    	}
    	
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(json);
    
    }
}
