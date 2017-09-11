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
import org.json.JSONObject;
import org.json.JSONArray;

public class RecipeManager {
	private static String db_name = "saveory_app";
	private static String db_client_uri = "mongodb://sapphires:saveoryArmory@sapphires-db.rtp.raleigh.ibm.com/saveory_app";
	private static String collection_name = "recipes";
	private static MongoClient mongoClient = new MongoClient(new MongoClientURI(db_client_uri));
	
	static HttpClient httpclient = HttpClientBuilder.create().build();
	
	
	public static String getUserPantry(String username){
		String user = RecipeManager.getUser(username);
		if(user.contains("failure") || user.contains("failed") || user.equals(""))
			return user;
		
		//Changes received JSON from array to object
		user = user.substring(1, user.length() - 1); 
    	
    	//Read in the requested pantry as a JSON
    	JSONObject userJSON = new JSONObject(user);
    	JSONArray pantryArray = userJSON.getJSONArray("pantry");
        
    	return pantryArray.toString();
	
	}
	
	public static String getUser(String username){
		String showUserResponse;
		HttpGet getUser = new HttpGet("http://pantry-service:9080/Pantry/pantry?user=" + username);

        try{
            //Execute request
            HttpResponse getUserResponse = httpclient.execute(getUser);
            HttpEntity entity = getUserResponse.getEntity();

            //Check if the response entity is there
            if(entity != null){
                    showUserResponse = EntityUtils.toString(entity);
            }
            else{
            	showUserResponse = "{\"status\":\"failure, no response entity from backend "
            						+ "when retrieving the user's pantry\"}";
            }
        }
        catch (Exception e) {
        	showUserResponse = "{\"status\":\"failed while executing GET request to the backend "
                					+ "for a recipe\", \"error\":\""+e.getMessage()+"\"}";
        }
        
        return showUserResponse;
	}
	
	public static String getRecipes(){
		MongoDatabase database = mongoClient.getDatabase(db_name);
		MongoCollection<Document> recipeCollection = database.getCollection(collection_name);
		
		//Document query = new Document().append("name","CAP");
		//Document query2 = new Document().append("ingredients",[]);
				
		//MongoCursor<Document> recipe = rCollection.find(query);
		
		MongoCursor<Document> recipeIterator = recipeCollection.find().iterator();
		
		BasicDBList list = new BasicDBList();
		while(recipeIterator.hasNext()){
			Document doc = recipeIterator.next();
			//list.add(doc);
			//Document current = Document.parse((String) doc.get("ingredients"));
			ArrayList<Document> currentIngredients = (ArrayList<Document>) doc.get("ingredients");
			for(Document current : currentIngredients)
				list.add(current);
		}
		
		//QueryParam test case
		//if(username != null)
			//list.add(new Document().append("username", username));
		return JSON.serialize(list);
	}
	
	public static Recipe parseRecipe(MongoCursor<Document> recipeIterator){
		//Empty Recipe Object
		Recipe recipe = new Recipe();
		
		//Sets name for recipe
		Document doc = recipeIterator.next();
		recipe.setName(doc.getString("\"name\""));
		
		//Sets Id for recipe
		doc = recipeIterator.next();
		recipe.setId(doc.getString("\"id\""));
		
		//Sets Author for recipe
		doc = recipeIterator.next();
		recipe.setAuthor(doc.getString("\"author\""));
		
		//Sets Tags for recipe
		doc = recipeIterator.next();
		ArrayList<String> tags = RecipeManager.parseTags(doc.getString("\"tags\""));
		
		//Sets Description for recipe
		doc = recipeIterator.next();
		recipe.setDescription(doc.getString("\"description\""));
		
		//Sets Instructions for recipe
		doc = recipeIterator.next();
		recipe.setInstructions(doc.getString("\"instructions\""));
		
		//Sets Ingredients for recipe
		doc = recipeIterator.next();
		recipe.setIngredients(RecipeIngredient.parseRecipeIngredient(doc.getString("\"ingredients\"")));
		
		return recipe; 
	}
	
	public static ArrayList<String> parseTags(String tags){
		//Empty object to be returned
		ArrayList<String> tagsList = new ArrayList<>();
		
		//Brackets and braces are removed
		tags = tags.substring(9, tags.length()-1);
		
		//Documents inside each ingredient objects are split
		String[] str = tags.split(",");
		
		//Iterates through the available tags
		for(int i = 0; i < str.length; i++){
			tagsList.add(str[i]);
		}
		return tagsList;
	}

}