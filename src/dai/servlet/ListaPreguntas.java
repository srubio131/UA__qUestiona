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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class ListaPreguntas extends HttpServlet {
	
	private JSONArray get_preguntas_dado_un_tema(String tema, DatastoreService datastore, Key userKey) throws JSONException
	{
		JSONArray preguntas = new JSONArray();
		JSONObject pregunta = null;
		Filter existe_tema = new FilterPredicate("tema", FilterOperator.EQUAL, tema);
	    Query q = new Query("Pregunta").setAncestor(userKey).setFilter(existe_tema).addSort("pregunta", SortDirection.ASCENDING);
	    PreparedQuery pq = datastore.prepare(q);
		
	    for (Entity result : pq.asIterable()) {
	    	pregunta = new JSONObject();
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
		 
		 JSONObject json = new JSONObject();
		 String tema = request.getParameter("tema");
		 try {
			json.put("result", get_preguntas_dado_un_tema(tema, datastore, userKey));
		} catch (JSONException e) {
			e.printStackTrace();
		}
    
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().print(json);
    }
}
