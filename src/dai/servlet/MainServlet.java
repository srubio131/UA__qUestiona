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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class MainServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
    	
      UserService userService = UserServiceFactory.getUserService();
      String thisURL = request.getRequestURI();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      
      if (request.getUserPrincipal() != null) {
        String userName = request.getUserPrincipal().getName();
        Key userKey = KeyFactory.createKey("Usuario", userName);        
        
        // Comprobar que el usuario existe        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Filter existe_usuario = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, userKey);
        Query q = new Query("Usuario").setFilter(existe_usuario);
        PreparedQuery pq = datastore.prepare(q);
        
        if (pq.asSingleEntity() == null)
        	datastore.put(new Entity("Usuario", userName));
        
        request.setAttribute("logout", userService.createLogoutURL(thisURL));
        request.setAttribute("usuario", userName);	  
        request.getRequestDispatcher("/main.jsp").forward(request, response);
              
      } else {        
        request.setAttribute("login", userService.createLoginURL(thisURL));
        request.getRequestDispatcher("/welcome.jsp").forward(request, response);
      }
    }
  }