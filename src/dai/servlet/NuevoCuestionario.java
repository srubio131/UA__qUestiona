package dai.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class NuevoCuestionario extends HttpServlet {
	
	private Key existe_cuestionario(String tema, DatastoreService datastore, Key userKey)
	{
		Key cuestionarioKey = null;
	    Filter existe_tema = new FilterPredicate("tema", FilterOperator.EQUAL, tema);
	    Query q = new Query("Cuestionario").setAncestor(userKey).setFilter(existe_tema);
	    PreparedQuery pq = datastore.prepare(q);
	    
		if (pq.asSingleEntity() != null)
			cuestionarioKey = pq.asSingleEntity().getKey();
		
		return cuestionarioKey;
	}
	
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
    	
    	String userName = request.getUserPrincipal().getName();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key userKey = KeyFactory.createKey("Usuario", userName);   	
    	
    	String tema = request.getParameter("tema");
    	JSONObject json = new JSONObject();
    	
    	try {
    		
    		Key cuestionarioKey = existe_cuestionario(tema, datastore, userKey);
    		
	    	if (tema.isEmpty())
	    		json.put("error", new JSONObject().put("message", "se esperaba el parámetro tema"));
	    	else if (cuestionarioKey != null)
	    		json.put("error", new JSONObject().put("message", "el tema " + tema + " ya existe en la base de datos"));
	    	else
	    	{
	    		Entity ent_tema = new Entity("Cuestionario", userKey);
	    		ent_tema.setProperty("tema", tema);	    		
	    		datastore.put(ent_tema);
	    		
	    		Key key_cuestionario = existe_cuestionario(tema, datastore, userKey);
	    		if (key_cuestionario != null)
	    			json.put("result", "[]");
	    		else
	    			json.put("error", new JSONObject().put("message", "el cuestionario NO ha podido ser añadido. Pruebe de nuevo"));
	    	}
	    	
    	} catch(JSONException e) {
    		e.printStackTrace();
    	}
    	
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(json);
    	
    }
}
