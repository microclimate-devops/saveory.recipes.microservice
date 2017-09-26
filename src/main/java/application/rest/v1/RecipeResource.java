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
import java.util.HashMap;
import java.util.Iterator;

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
import javax.json.JsonArray;
import javax.json.JsonReader;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
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
//import com.ibm.json.java.JSONObject;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;
import java.io.IOException;
import java.io.StringReader;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.mongojack.*;
import com.mongodb.DBCollection;
//import org.mongodb.DB;
import com.mongodb.DBObject;

@Path("recipes")
public class RecipeResource {
		private static String db_name = "saveory_app";
		private static String db_client_uri = "mongodb://sapphires:saveoryArmory@sapphires-db.rtp.raleigh.ibm.com/saveory_app";
		private static String collection_name = "recipes";
		private static String auxCollection = "users";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(db_client_uri));
        

    	@GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response getRecipes() throws JsonParseException, JsonMappingException, IOException{
        	//Retrievement of the mongo database and recipe collection
        	MongoDatabase database = mongoClient.getDatabase(db_name);
        	MongoCollection<Document> recipeCollection = database.getCollection(collection_name);
        		
        	//Iterator used to go through recipe collection JSONs 
        	MongoCursor<Document> recipeIterator = recipeCollection.find().iterator();
        	
        	//Pantry service request to get current user's pantry (hard coded currently and just for tracing)
        	HashMap<String, Double> userIngredients = RecipeManager.getUserIngredients("59bae6bc46e0fb00012e87b5");
        	
        	//List to add all of the recipes
        	BasicDBList list = new BasicDBList();
        	
        	//ArrayList and Document variables for next iterations
        	ArrayList<Document> currentRecipeIngredients;
        	Document currentIngredient;
        	
        	//Iteration continues while the iterator still has documents
        	while(recipeIterator.hasNext()){
        		//Holds next document of the current recipe JSONObject
        		Document currentRecipe = recipeIterator.next();
        		
    			//We get the current recipe ingredients convert its value into an ArrayList of Documents
    			currentRecipeIngredients =  (ArrayList<Document>) currentRecipe.get("ingredients");
    			
    			//We iterate through its JSONObjects
    			for(int i = 0; i < currentRecipeIngredients.size(); i++){
        				
    				//We hold the current ingredient in a variable
    				currentIngredient = currentRecipeIngredients.get(i);
    				
    				//We try to obtain the quantity of this ingredient that the user has in the pantry
    				Double currentQuantity = userIngredients.get(currentIngredient.getString("name").toLowerCase());
    				
    				//Verifies if the user has the ingredient
    				if(currentQuantity != null){
    					
    					//If the user has more or equal quantity needed 
    					if(currentQuantity >= Double.parseDouble(currentIngredient.getString("quantity"))){
		    				//Appends a value that validates if the user has enough ingredients
	    					currentIngredient.append("has", "2");
    					}
    					else{
    						//The user has the ingredient but not enough quantity
    						currentIngredient.append("has", "1");
    					}
    				}
    				else{
    					//Ingredient is not inside the user's pantry
    					currentIngredient.append("has", "0");
    				}
    				//Modified ingredient Document is set into the current index in the ArrayList
    				currentRecipeIngredients.set(i, currentIngredient);
    				
        		}
    			//New Modified ArrayList replaces the old ArrayList
    			currentRecipe.replace("ingredients", currentRecipeIngredients);
        		
        		//Current modified Recipe is added into the list to return
        		list.add(currentRecipe);
        	}
        	//Modified Recipe List is returned
        	return Response.ok(list.toString()).build();
        }
        
        @GET
        @Path("/test6")
        @Produces(MediaType.TEXT_PLAIN)
        public String getRecipes2() throws JsonParseException, JsonMappingException, IOException{
        	//Retrievement of the mongo database and recipe collection
        	MongoDatabase database = mongoClient.getDatabase(db_name);
        	MongoCollection<Document> recipeCollection = database.getCollection(collection_name);
        		
        	//Iterator used to go through recipe collection JSONs 
        	MongoCursor<Document> recipeIterator = recipeCollection.find().iterator();
        	
        	//Pantry service request to get current user's pantry (hard coded currently and just for tracing)
        	HashMap<String, Double> userIngredients = RecipeManager.getUserIngredients("59bae6bc46e0fb00012e87b5");
        	
        	//List to add all of the recipes
        	BasicDBList list = new BasicDBList();
        	
        	//ArrayList and Document variables for next iterations
        	ArrayList<Document> currentRecipeIngredients;
        	Document currentIngredient;
        	
        	//Iteration continues while the iterator still has documents
        	while(recipeIterator.hasNext()){
        		//Holds next document of the current recipe JSONObject
        		Document currentRecipe = recipeIterator.next();
        		
    			//We get the current recipe ingredients convert its value into an ArrayList of Documents
    			currentRecipeIngredients =  (ArrayList<Document>) currentRecipe.get("ingredients");
    			
    			//We iterate through its JSONObjects
    			for(int i = 0; i < currentRecipeIngredients.size(); i++){
        				
    				//We hold the current ingredient in a variable
    				currentIngredient = currentRecipeIngredients.get(i);
    				
    				//We try to obtain the quantity of this ingredient that the user has in the pantry
    				Double currentQuantity = userIngredients.get(currentIngredient.getString("name").toLowerCase());
    				
    				//Verifies if the user has the ingredient
    				if(currentQuantity != null){
    					
    					//If the user has more or equal quantity needed 
    					if(currentQuantity >= Double.parseDouble(currentIngredient.getString("quantity"))){
		    				//Appends a value that validates if the user has enough ingredients
	    					currentIngredient.append("has", "2");
    					}
    					else{
    						//The user has the ingredient but not enough quantity
    						currentIngredient.append("has", "1");
    					}
    				}
    				
    				else{
    					//Ingredient is not inside the user's pantry
    					currentIngredient.append("has", "0");
    				}
    				//Modified ingredient Document is set into the current index in the ArrayList
    				currentRecipeIngredients.set(i, currentIngredient);
    				
        		}
    			//New Modified ArrayList replaces the old ArrayList
    			currentRecipe.replace("ingredients", currentRecipeIngredients);
        		
        		//Current modified Recipe is added into the list to return
        		list.add(currentRecipe);
        	}
        	//Modified Recipe List is returned
        	return list.toString();
        }
        
        
         @GET
         @Path("/{recipeId}")
         @Produces(MediaType.APPLICATION_JSON)
         public Response getRecipe(@PathParam("recipeId") String query){
                //Retrievement of the mongo database and recipe collection
            	MongoDatabase database = mongoClient.getDatabase(db_name);
            	MongoCollection<Document> recipeCollection = database.getCollection(collection_name);
            	
            	//Query is converted to a document
            	Document queryDoc = new Document("id", query);
            	
            	//Query Document is used to find the corresponding recipe
            	MongoCursor<Document> recipe = recipeCollection.find(queryDoc).iterator();
            		
            	//If no recipe is found no content response is returned
            	if(!recipe.hasNext())
            		return Response.noContent().build();
            	
            	//Found recipe is returned to the user
            	return Response.ok(recipe.next()).build();
            	
            	
                /*HttpGet getRecipe = new HttpGet("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/queries/analyze?q=" + query);
                getRecipe.addHeader("X-Mashape-Key", "EL3PByCPCAmshIKkZlrZvwsXPgVVp1PKD3MjsnhRbGZj3YLPli");
                getRecipe.addHeader("X-Mashape-Host", "spoonacular-recipe-food-nutrition-v1.p.mashape.com");
                HttpResponse getRecipeResponse;

                try{
                        //Execute request
                        getRecipeResponse = httpclient.execute(getRecipe);
                        HttpEntity respEntity = getRecipeResponse.getEntity();

                        //Check if the response entity is there
                        if(respEntity != null){
                                showRecipeResponse = EntityUtils.toString(respEntity);
                        }else{
                        	showRecipeResponse = "{\"status\":\"failure, no response entity from backend when retrieving a recipe\"}";
                        }
                }
                catch (Exception e) {
                        showRecipeResponse = "{\"status\":\"failed while executing GET request to the backend for a recipe\", \"error\":\""+e.getMessage()+"\"}";
                }

                return Response.ok(showRecipeResponse).build();

                /*HttpResponse<JsonNode> response = Unirest.get("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/queries/analyze?q=salmon+with+fusilli+and+no+nuts")
                .header("X-Mashape-Key", "EL3PByCPCAmshIKkZlrZvwsXPgVVp1PKD3MjsnhRbGZj3YLPli")
                .header("X-Mashape-Host", "spoonacular-recipe-food-nutrition-v1.p.mashape.com")
                .asJson();
                JSONObject recipe = response.getBody().getObject();
                //JSONArray results = responsejson.getJSONArray("results");
                return recipe;
                }
                catch (Exception e){
                //HttpResponse<JsonNode> response = null;
                return "Nope";
                 */
        }
}
