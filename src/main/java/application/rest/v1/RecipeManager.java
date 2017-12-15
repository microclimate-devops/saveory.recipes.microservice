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
	private static String db_name = System.getenv("MONGO_DATABASE_NAME");
	private static String db_client_uri = "mongodb://"+System.getenv("MONGO_USER")+":"+System.getenv("MONGO_PWD")+"@"+
		System.getenv("MONGO_HOST")+":"+System.getenv("MONGO_PORT")+"/"+RecipeManager.db_name;
	private static String collection_name = "recipes";
	private static MongoClient mongoClient = new MongoClient(new MongoClientURI(db_client_uri));
	
	static HttpClient httpclient = HttpClientBuilder.create().build();
	
	
	public static JSONArray getUserPantry(String user_token){
		String showPantryResponse = "";
		JSONArray pantryArray = new JSONArray();
		HttpGet getPantry = new HttpGet("http://pantry-service:9080/Pantry/pantry/" + user_token);
		try{
            //Execute request
            HttpResponse getPantryResponse = httpclient.execute(getPantry);
            HttpEntity entity = getPantryResponse.getEntity();

            //Check if the response entity is there
            if(entity != null){
                    showPantryResponse = EntityUtils.toString(entity);
                    
                    //It is converted into a JSONObject
                	JSONObject pantryJSON = new JSONObject(showPantryResponse);
                	
                	//We get the pantry array from the JSONObject
                	pantryArray = (JSONArray) pantryJSON.get("pantry");
            }
            //Edit response to failure and no response
            else{
            	showPantryResponse = "{\"Status\":\"Failure, no response entity from backend "
            						+ "when retrieving the user's pantry\"}";
            }
        }
		//Catch possible exception
        catch (Exception e) {
        	showPantryResponse = "{\"Status\":\"Failed while executing GET request to the pantry backend service"
                					+ "for a recipe\", \"error\":\""+e.getMessage()+"\"}";
        }
		
		return pantryArray;
	}
	
	public static HashMap<String, Double> getUserIngredients(String user_token){
		//We create the Map to be returned with the ingredients and their quantity
		HashMap<String, Double> ingredients = new HashMap<>();
		
		//We get the pantry array from the JSONObject
    	JSONArray pantryArray = getUserPantry(user_token);
    	
    	//Variable to iterate
    	JSONObject curr;
    	
    	//We Iterate through the pantryJSON to get the ingredient names
    	for(int j = 0; j < pantryArray.length(); j++){
    		
    		//Hold current in a JSONObject variable
    		curr = (JSONObject) pantryArray.get(j);
    		
    		//Store current JSONObject ingredient name in pantryIngredients
    		ingredients.put(curr.getString("ingredient").toLowerCase(), curr.getDouble("quantity"));
    	}
    	
    	
    	return ingredients;
	}
	
	public static HashMap<String, String> getUserIngredientsWords(String user_token){
		//We create the Map to be returned with the ingredients and their quantity
				HashMap<String, String> ingredientsWords = new HashMap<>();
				String currentName;
				String[] words;
				//We get the pantry array from the JSONObject
		    	JSONArray pantryArray = getUserPantry(user_token);
		    	
		    	//Variable to iterate
		    	JSONObject currentIngredient;
		    	
		    	//We Iterate through the pantryJSON to get the ingredient names
		    	for(int j = 0; j < pantryArray.length(); j++){
		    		
		    		//Hold current in a JSONObject variable
		    		currentIngredient = (JSONObject) pantryArray.get(j);
		    		
		    		//Hold the name of the current ingredient
		    		currentName = currentIngredient.getString("ingredient");
		    		
		    		//We split this name into separate words
		    		words = currentName.toLowerCase().split(" ");
		    		 
		    		//We iterate through this words to add them into a map which has a key for each word
		    		//and a value that references to the original word string
		    		for(int i = 0; i < words.length; i++)
		    			ingredientsWords.put(words[i], currentName);
		    		
		    	}
		    	
		    	//Return the split name to full name map
		    	return ingredientsWords;
	}
	
	
	public static String getRecipes(){
		//Database is obtained through the mongo client
		MongoDatabase database = mongoClient.getDatabase(db_name);
		MongoCollection<Document> recipeCollection = database.getCollection(collection_name);
		
		//We obtain an iterator using a the mongo find method
		MongoCursor<Document> recipeIterator = recipeCollection.find().iterator();
		
		//A DB list is created, this will hold all of the found recipes
		BasicDBList list = new BasicDBList();
		
		//This will run meanwhile the iterator has a next recipe
		while(recipeIterator.hasNext()){
			//Current recipe is saved into a variable
			Document doc = recipeIterator.next();
			
			//Ingredients from the current recipe are obtained as an ArrayList of documents
			ArrayList<Document> currentIngredients = (ArrayList<Document>) doc.get("ingredients");
			
			//The ingredients are added into the DB list
			for(Document current : currentIngredients)
				list.add(current); 
		}
		//The list with the recipes is returned
		return JSON.serialize(list);
	}
	
//	public static Recipe parseRecipe(MongoCursor<Document> recipeIterator){
//		//Empty Recipe Object
//		Recipe recipe = new Recipe();
//		
//		//Sets name for recipe
//		Document doc = recipeIterator.next();
//		recipe.setName(doc.getString("\"name\""));
//		
//		//Sets Id for recipe
//		doc = recipeIterator.next();
//		recipe.setId(doc.getString("\"id\""));
//		
//		//Sets Author for recipe
//		doc = recipeIterator.next();
//		recipe.setAuthor(doc.getString("\"author\""));
//		
//		//Sets Tags for recipe
//		doc = recipeIterator.next();
//		ArrayList<String> tags = RecipeManager.parseTags(doc.getString("\"tags\""));
//		
//		//Sets Description for recipe
//		doc = recipeIterator.next();
//		recipe.setDescription(doc.getString("\"description\""));
//		
//		//Sets Instructions for recipe
//		doc = recipeIterator.next();
//		recipe.setInstructions(doc.getString("\"instructions\""));
//		
//		//Sets Ingredients for recipe
//		doc = recipeIterator.next();
//		recipe.setIngredients(RecipeIngredient.parseRecipeIngredient(doc.getString("\"ingredients\"")));
//		
//		return recipe; 
//	}
//	
	////////////////////////////////////////Not used////////////////////////////////////////////////////
	public static void addToArray(Recipe[] list, Recipe recipe, int index){
		list[index] = recipe;
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
