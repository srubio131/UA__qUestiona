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
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class ListaCuestionarios extends HttpServlet {
	
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
    	
    	 String userName = request.getUserPrincipal().getName();
		 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		 Key userKey = KeyFactory.createKey("Usuario", userName);   	
	     Query q = new Query("Cuestionario").setAncestor(userKey).addSort("tema", SortDirection.ASCENDING);
	     PreparedQuery pq = datastore.prepare(q);   
	     
	     JSONArray arr = new JSONArray();
	     JSONObject json = new JSONObject();
	     
	     for (Entity result : pq.asIterable()) {
	    	 arr.put(result.getProperty("tema").toString());
	     }
	     
		 try {		 
			 
			 json.put("result", arr);
			 
		} catch (JSONException e) {
			//response.getWriter().print(json.put("result", new JSONArray()));
		}
		 
		 response.setContentType("application/json");
	     response.setCharacterEncoding("UTF-8");
	     response.getWriter().print(json);
    }
}
