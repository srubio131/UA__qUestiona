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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class BorraCuestionario extends HttpServlet {
	
	private Key existe_cuestionario(String nombre_tema, DatastoreService datastore, Key userKey)
	{
		Key temaKey = null;
	    Filter existe_tema = new FilterPredicate("tema", FilterOperator.EQUAL, nombre_tema);
	    Query q = new Query("Cuestionario").setAncestor(userKey).setFilter(existe_tema);
	    PreparedQuery pq = datastore.prepare(q);
	    
		if (pq.asSingleEntity() != null)
			temaKey = pq.asSingleEntity().getKey();
		
		return temaKey;
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
    		
    		Key temaKey = existe_cuestionario(tema, datastore, userKey);
    		
	    	if (tema.isEmpty())
	    		json.put("error", new JSONObject().put("message", "se esperaba el parámetro tema"));
	    	else if (temaKey == null)
	    		json.put("error", new JSONObject().put("message", "el tema " + tema + " NO existe en la base de datos"));
	    	else
	    	{ 		
	    		datastore.delete(temaKey);	    		
	    		
	    		Key key_tema = existe_cuestionario(tema, datastore, userKey);
	    		if (key_tema == null)
	    			json.put("result", "[]");
	    		else
	    			json.put("error", new JSONObject().put("message", "el cuestionario NO ha podido ser borrado"));
	    	}
	    	
    	} catch(JSONException e) {
    		e.printStackTrace();
    	}
    	
    	response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(json);
        
    }
}
