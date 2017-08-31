package application.rest.v1;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
import java.util.HashMap;


public class RecipeManager {
	static HttpClient httpclient = HttpClientBuilder.create().build();
	
	public static String getUserPantry(String username){
		String showPantryResponse;
		HttpGet getUserPantry = new HttpGet("http://pantry-service:9080/Pantry/pantry?user=" + username);

        try{
            //Execute request
            HttpResponse getUserPantryResponse = httpclient.execute(getUserPantry);
            HttpEntity respEntity = getUserPantryResponse.getEntity();

            //Check if the response entity is there
            if(respEntity != null){
                    showPantryResponse = EntityUtils.toString(respEntity);
            }
            else{
            	showPantryResponse = "{\"status\":\"failure, no response entity from backend "
            						+ "when retrieving the user's pantry\"}";
            }
        }
        catch (Exception e) {
        	showPantryResponse = "{\"status\":\"failed while executing GET request to the backend "
                					+ "for a recipe\", \"error\":\""+e.getMessage()+"\"}";
        }
        
        return showPantryResponse;
        
        
	
	}
	
	
	
	

}