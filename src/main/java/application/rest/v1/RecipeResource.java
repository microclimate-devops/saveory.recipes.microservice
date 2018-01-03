package application.rest.v1;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

@Path("recipes")
public class RecipeResource {
		private static String db_name = System.getenv("MONGO_DATABASE_NAME");
		private static String db_client_uri = "mongodb://"+System.getenv("MONGO_USER")+":"+System.getenv("MONGO_PWD")+"@"+
			System.getenv("MONGO_HOST")+":"+System.getenv("MONGO_PORT")+"/"+RecipeResource.db_name;
		private static String collection_name = "recipes";
		private static String aux_collection = "users";
		private static String yummly_ID = System.getenv("YUMMLY_ID");
		private static String yummly_key = System.getenv("YUMMLY_KEY");
		
		MongoClient mongoClient = new MongoClient(new MongoClientURI(db_client_uri));
        

		@GET
        @Produces(MediaType.APPLICATION_JSON)
        public Response getRecipes( 
        	//@HeaderParam("user_token") String user_token,
        	@QueryParam("user_token") String user_token, //For development purposes
        	@QueryParam("search") String search) throws JsonParseException, JsonMappingException, IOException{
        	//Retrievement of the mongo database and recipe collection
        	MongoDatabase database = mongoClient.getDatabase(db_name);
        	MongoCollection<Document> recipeCollection = database.getCollection(collection_name);
        	MongoCursor<Document> recipeIterator;
        	
        	//If the user does a query
        	if(search != null){
        		if(!search.isEmpty()){
        		//Creation of query document using a logical or string array search
        		Document query = new Document("$text", new Document("$search", search));
        		
        		//Query document is used to find all recipes with similar name
        		recipeIterator = recipeCollection.find(query).iterator();
        		}
        		else{
        			return Response.noContent().build();
        		}
        	}
        	//If there is no query
        	else{
	        	//Iterator used to go through recipe collection JSONs 
	        	recipeIterator = recipeCollection.find().iterator();
        	}
        	
        	//Pantry service request to get current user's pantry (hard coded currently and just for tracing)
        	HashMap<String, Double> userIngredients = RecipeManager.getUserIngredients(user_token);
        	HashMap<String, String> userIngredientWords = RecipeManager.getUserIngredientsWords(user_token);
        	
        	//List to add all of the recipes
        	BasicDBList list = new BasicDBList();
        	
        	//ArrayList and Document variables for next iterations
        	ArrayList<Document> currentRecipeIngredients = new ArrayList<>();
        	ArrayList<String> currentRecipeIngredientsAPI = new ArrayList<>();
        	Document currentIngredient;
        	ArrayList<String> hasList = new ArrayList<>();
        	HashSet<String> matchingIngredients = new HashSet<>();
        	String[] currentWordSplit;
        	String match;
        	//Iteration continues while the iterator still has documents
        	while(recipeIterator.hasNext()){
        		
        		//Holds next document of the current recipe JSONObject
        		Document currentRecipe = recipeIterator.next();
        		
        		try{
        			//We get the current recipe ingredients convert its value into an ArrayList of Strings
        			currentRecipeIngredientsAPI = (ArrayList<String>) currentRecipe.get("ingredients");
        			
        			//We iterate through its Strings
	    			for(String currentIngredientAPI : currentRecipeIngredientsAPI){
	    				
	    				//We try to obtain the quantity of this ingredient that the user has in the pantry
	    				Double currentQuantity = userIngredients.get(currentIngredientAPI.toLowerCase());
	    				
	    				//Verifies if the user has the ingredient
	    				if(currentQuantity != null){
	    					//User has ingredient
	    					hasList.add("1");
	    					
	    					//Current Ingredient name is split into words
	    					currentWordSplit = currentIngredientAPI.toLowerCase().split(" ");
	    					
	    					//We verify if any of these words are found in the user ingredient words
	    					for (int i = 0; i < currentWordSplit.length; i++){
	    						match = userIngredientWords.get(currentWordSplit[i]);
	    						//If one is equivalent we add it to the matching ingredients list
	    						if(match != null)
	    							matchingIngredients.add(match);
	    					}
	    				}
	    				else{
	    					//Ingredient is not inside the user's pantry
	    					hasList.add("0");
	    				}
	    			}
	    			//New hasList and matchingIngredients ArrayList is added to the Recipe
	    			currentRecipe.append("has", hasList.toArray());
	    			currentRecipe.append("matchingIngredients", matchingIngredients.toArray());
	        		
	    			//Prepare hasList for next iteration
	    			hasList.clear();
	    			matchingIngredients.clear();
	    			
	        		}
        		
        		//Catches exception of the recipe list being one of the older versions
        		catch(Exception e){
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
	    					
	    					//If user has the ingredient a 1 is added to a hasList referencing this ingredients index
	    					//currentIngredient.append("has", "1");
	    					hasList.add("1");
	    					
	    					//Current Ingredient name is split into words
	    					currentWordSplit = currentIngredient.getString("name").toLowerCase().split(" ");
	    					
	    					//We verify if any of these words are found in the user ingredient words
	    					for (int j = 0; j < currentWordSplit.length; j++){
	    						match = userIngredientWords.get(currentWordSplit[j]);
	    						//If one is equivalent we add it to the matching ingredients list
	    						if(match != null)
	    							matchingIngredients.add(match);
	    					}
	    					//If the user has more or equal quantity needed 
	    					//if(currentQuantity >= Double.parseDouble(currentIngredient.getString("quantity"))){
			    			//	//Appends a value that validates if the user has enough ingredients
		    				//	currentIngredient.append("has", "2");
	    					//}
	    					//else{
	    					//	//The user has the ingredient but not enough quantity
	    					//	currentIngredient.append("has", "1");
	    					//} 
	    				}
	    				else{
	    					//Ingredient is not inside the user's pantry
	    					//currentIngredient.append("has", "0");
	    					hasList.add("0");
	    				}
	    				//Modified ingredient Document is set into the current index in the ArrayList
	    				currentRecipeIngredients.set(i, currentIngredient);
	    			}
	    			//New matchingIngredients ArrayList is added to the Recipe
    				currentRecipe.append("hasList", hasList.toArray());
    				currentRecipe.append("matchingIngredients", matchingIngredients.toArray());
	    			
	    			//Prepare hasList for next iteration
	    			hasList.clear();
	    			matchingIngredients.clear();
        		}
    			//New Modified ArrayList replaces the old ArrayList
    			if(currentRecipeIngredientsAPI.isEmpty())
        			currentRecipe.replace("ingredients", currentRecipeIngredients);
    			else
    				currentRecipe.replace("ingredients", currentRecipeIngredientsAPI);
    			
        		//Current modified Recipe is added into the list to return
        		list.add(currentRecipe);
        	}
        	//Modified Recipe List is returned
        	return Response.ok(list.toString()).build();
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
        	return Response.ok(recipe.next().toJson()).build();
        }
        
        
//////////////////////////////////Features Under Development/////////////////////////////////////////////////        
        
        @GET
    	@Path("/test")
    	public Response addUser(@HeaderParam("user_token") String user_token) {
        	//Retrievement of the mongo database and recipe collection
        	MongoDatabase database = mongoClient.getDatabase(db_name);
        	MongoCollection<Document> userCollection = database.getCollection(aux_collection);
        	
        	//Query is converted to a document
        	Document queryDoc = new Document("_id", new ObjectId(user_token));
        	
        	//Query Document is used to find the corresponding recipe
        	MongoCursor<Document> userIterator = userCollection.find(queryDoc).iterator();
        	
        	//Verifies if a user was found
        	if(userIterator.hasNext()){
        		//Gets found user
        		Document user = userIterator.next();
        		//Returns its name
        		return Response.status(200)
            			.entity(user_token + " is called, username : " + user.getString("name"))
            			.build();
        	}
        	//If no user was found returns a no content Response
    		return Response.status(204)
    			.entity(user_token + " user was not found")
    			.build();

    	}
        
        @GET
    	@Path("/test2")
        @Produces(MediaType.APPLICATION_JSON)
    	public Response yummlyTest(@QueryParam("q") String q){
        	//Retrievement of the mongo database and recipe collection
        	MongoDatabase database = mongoClient.getDatabase(db_name);
        	MongoCollection<Document> recipesCollection = database.getCollection(collection_name);
        	
        	//Variable Definitions
        	String apiResponse;
        	BasicDBList list = new BasicDBList();
        	BasicDBObject apiObject = new BasicDBObject();
        	
        	//Http Client Setup
        	HttpClient httpclient = HttpClientBuilder.create().build();
        	HttpGet request = new HttpGet("http://api.yummly.com/v1/api/recipes?"
        			+ "_app_id=" + yummly_ID + "&_app_key=" + yummly_key + "&maxResult=100&q=" + q);
        	
        	
        	//Recipe variables (//X = Available)
        	String name; //X
        	String id; //X
        	String _id;
        	String author; //X
        	ArrayList<BasicDBObject> tag; //X
        	String description;
        	String instructions;
        	List<BasicDBObject> ingredients; //X
        	String time; //X
        	List<String> imageURLList; //X
        	String imageURL;//X
        	
        	//Variables used to parse/add recipes to return
        	BasicDBObject parsedRecipe = new BasicDBObject();
        	Document verificationQuery = new Document();
        	MongoCursor<Document> recipeIterator;
        	//int idCounter = 0; Currently using yummlyAPI id instead
        	
        	try{
                //Execute request
                HttpResponse getAPIResponse = httpclient.execute(request);
                HttpEntity entity = getAPIResponse.getEntity();

                //Check if the response entity is there
                if(entity != null){
                        apiResponse = EntityUtils.toString(entity);
                        
                        //It is converted into a BasicDBObject
                    	apiObject = (BasicDBObject) JSON.parse(apiResponse);
                       
                    	//We retrieve the recipes (inside "matches" array) and assign it to an ArrayList<BasicDBObject> 
                    	ArrayList<BasicDBObject> recipeList = (ArrayList<BasicDBObject>) apiObject.get("matches");
                    	
                    	//We iterate through the recipes to parse it into our recipe object format
                    	for(BasicDBObject current : recipeList){ 
                    		
                    		//Get the desired recipe values from the current BasicDBObject
                    		name = current.getString("recipeName");
                    		author = current.getString("sourceDisplayName");
                    		BasicDBObject attributes = (BasicDBObject) JSON.parse(current.getString("attributes"));
                    		tag = (ArrayList<BasicDBObject>) attributes.get("course");
                    		id = current.getString("id");
                    		ingredients = (ArrayList<BasicDBObject>) current.get("ingredients");
                    		time = current.getString("totalTimeInSeconds");
                    		imageURLList = (ArrayList<String>) current.get("smallImageUrls");
                    		imageURL = imageURLList.get(0);
                    		imageURL = imageURL.substring(0, imageURL.length()-3).concat("-c");
                    		
                    		//A BsonObject is formed with the retrieved values
                    		parsedRecipe.append("name", name)
                    					.append("id", id)
                    					.append("author", author)
                    					.append("tag", tag)
                    					.append("ingredients", ingredients)
                    					.append("time", time)
                    					.append("imageURL", imageURL);
                    		
                    		//Adds recipe into mongoDB recipe collection and into the list to return
                    		verificationQuery.append("id", id);
                    		
                    		//We verify if this recipe does not already exist in the recipes Collection
                    		recipeIterator = recipesCollection.find(verificationQuery).iterator();
                    		if(!recipeIterator.hasNext())
                    			recipesCollection.insertOne(Document.parse(parsedRecipe.toString()));
                    		
                    		//We add the recipe to our list to return
                    		list.add(parsedRecipe);
                    		
                    		//Maintains the recipes id correctly with respect to the recipes being added
                    		//idCounter++;
                    	}
                    	
                    	//The list is converted into a string for the response
                    	apiResponse = list.toString();
                    	
                }
                //Edit response to failure and no response
                else{
                	apiResponse = "{\"Status\":\"Failure, no response entity from backend "
                						+ "when retrieving recipes from Yummly\"}";
                }
            }
    		//Catch possible exception
            catch (Exception e) {
            	String stackTrace = "";
            	for(StackTraceElement current : e.getStackTrace())
            		stackTrace = stackTrace + current.toString() + "\n";
            	apiResponse = "{\"Status\":\"Failed while executing GET request to the Yummly API\""
                    					+ ", \"Error\":\""+e.getMessage()+"\""
            							+ ", \"Stack Trace\":\"" + stackTrace + "\"}";
            }
        	//Builds and returns response
    		return Response.ok(apiResponse).build();
    	}
        
        @GET
    	@Path("/test3")
      //@Consumes(MediaType.APPLICATION_JSON) 
        @Produces(MediaType.APPLICATION_JSON)
    	public Response advancedSearch(){
        	
        	//Http Client Setup
        	HttpClient httpclient = HttpClientBuilder.create().build();
        	HttpGet request = new HttpGet("http://localhost:9080/RecipeService/recipes/test4");
 
        	//Variable Definitions
        	String apiResponse;
        	String yummlyRequest = "http://api.yummly.com/v1/api/recipes?"
        			+ "_app_id=" + yummly_ID + "&_app_key=" + yummly_key;
        	BasicDBObject apiObject = new BasicDBObject();
        	ArrayList<String> allowedIngredients;
        	ArrayList<String> excludedIngredients;
        	ArrayList<String> allowedAllergies;
        	int loopSize;
        	
        	try{
                //Execute request
                HttpResponse getAPIResponse = httpclient.execute(request);
                HttpEntity entity = getAPIResponse.getEntity();

                //Check if the response entity is there
                if(entity != null){
                	apiResponse = EntityUtils.toString(entity);
                    
                    //It is converted into a BasicDBObject
                	apiObject = (BasicDBObject) JSON.parse(apiResponse);
                	
                	allowedIngredients = (ArrayList<String>) apiObject.get("allowedIngredients");
                	excludedIngredients = (ArrayList<String>) apiObject.get("excludedIngredients");
                	allowedAllergies = (ArrayList<String>) apiObject.get("allowedAllergies");
                	
                	for(String current : allowedIngredients)
                		yummlyRequest = yummlyRequest.concat("&allowedIngredient[]=" + current);
                	for(String current : excludedIngredients)
                		yummlyRequest = yummlyRequest.concat("&excludedIngredient[]=" + current);
                	for(String current : allowedAllergies)
                		yummlyRequest = yummlyRequest.concat("&allowedAllergy[]=" + current);
                	
                	//Execute request
                	request.reset();
                	request = new HttpGet(yummlyRequest);
                    getAPIResponse = httpclient.execute(request);
                    entity = getAPIResponse.getEntity();
                    
                    if(entity != null){
                    	BasicDBObject yummlyResponse = BasicDBObject.parse(EntityUtils.toString(entity));
                    	yummlyResponse.append("url", yummlyRequest);
                    	apiResponse = yummlyResponse.toString();
                    	//apiResponse = EntityUtils.toString(entity);
                    }
                    
                    else{
                    	apiResponse = "{\"Status\":\"Failure, no response entity from backend "
        						+ "when retrieving recipes from Yummly\"}";
                    }
	                	
                }
                //Edit response to failure and no response
                else{
                	apiResponse = "{\"Status\":\"Failure, no response entity from backend "
                						+ "when retrieving recipes from Yummly\"}";
                }
            }
    		//Catch possible exception
            catch (Exception e) {
            	String stackTrace = "";
            	for(StackTraceElement current : e.getStackTrace())
            		stackTrace = stackTrace + current.toString() + "\n";
            	apiResponse = "{\"Status\":\"Failed while executing GET request to the Yummly API\""
                    					+ ", \"Error\":\""+e.getMessage()+"\""
            							+ ", \"Stack Trace\":\"" + stackTrace + "\"}";
            }
        	
        	
        	return Response.ok(apiResponse).build();
        }
        
        @GET
    	@Path("/test4")
        @Produces(MediaType.APPLICATION_JSON)
    	public Response filterParameters(){
        	BasicDBObject filters = new BasicDBObject();
        	ArrayList<String> allowedIngredients = new ArrayList<>();
        	ArrayList<String> excludedIngredients = new ArrayList<>();
        	ArrayList<String> allowedAllergies = new ArrayList<>();
        	
        	allowedIngredients.add("coconut");
        	allowedIngredients.add("chocolate");
        	excludedIngredients.add("pepper");
        	allowedAllergies.add("396%5EDairy-Free");
        	
        	filters.append("allowedIngredients", allowedIngredients);
        	filters.append("excludedIngredients", excludedIngredients);
        	filters.append("allowedAllergies", allowedAllergies);
        	
        	return Response.ok(filters.toString()).build();
        
        }
        
}
