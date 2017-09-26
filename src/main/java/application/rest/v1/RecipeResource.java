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
        public String getRecipes(){
        	//Retrievement of the mongo database and recipe collection
        	MongoDatabase database = mongoClient.getDatabase(db_name);
        	MongoCollection<Document> recipeCollection = database.getCollection(collection_name);
        		
        	//Iterator used to go through recipe collection JSONs 
        	MongoCursor<Document> recipeIterator = recipeCollection.find().iterator();
        	
        	//Pantry service request to get current user's pantry (hard coded currently and just for tracing)
        	String pantry = RecipeManager.getUserPantry("59bae6bc46e0fb00012e87b5");
        	
        	//List to add all of the recipes
        	BasicDBList list = new BasicDBList();
        		
        	//Iteration continues while the iterator still has documents
        	while(recipeIterator.hasNext()){
        		
        		//Current document is added into the list
        		list.add(recipeIterator.next());
        	}
        		
        	//After all documents are added into the list then it is serialized into a string
        	return JSON.serialize(list);
        }
        
        @GET
        @Path("/test")
        @Produces(MediaType.APPLICATION_JSON)
        public String getRecipeJSON(
        		@QueryParam("username") String username
        		){
        	MongoDatabase database = mongoClient.getDatabase(db_name);
    		MongoCollection<Document> recipeCollection = database.getCollection(collection_name);
    		MongoCollection<Document> userCollection = database.getCollection(auxCollection);
    		
    		//Document query = new Document().append("name","CAP");
    		//Document query2 = new Document().append("ingredients",[]);
    				
    		//MongoCursor<Document> recipe = rCollection.find(query);
    		
    		MongoCursor<Document> recipeIterator = recipeCollection.find().iterator();
    		//MongoCursor<Document> userIterator = userCollection.find().iterator();
    		
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
        
        @GET
        @Path("/test2")
        @Produces(MediaType.APPLICATION_JSON)
        public Response getUserPantry(
        		@QueryParam("username") String username
        		){
        	
        	//Gets the user's pantry ingredients using the username in the query
        	String pantry = RecipeManager.getUserPantry(username);
//        	JSONObject userPantry = (JSONObject) JSON.parse(pantry);
//        	ObjectMapper pantryMapper = new ObjectMapper();
//        	pantryMapper.readValue(userPantry, Pantry);
        	//Get 1 recipe to compare its ingredients with the user's **TODO**
        	
        	
        	return Response.ok(pantry).build();
        }
        
        @GET
        @Path("/test3")
        @Produces(MediaType.APPLICATION_JSON)
        public Response getRecipeWMAP(
        		@QueryParam("recipeId") String query){
        	Recipe r1;
        	ObjectMapper mapper = new ObjectMapper();
        	ObjectWriter printer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        	try{
        	r1 = mapper.readValue("{"
        		+ "\"name\" : \"Cereal and Milk\","
        				+ "\"id\" : \"1\","
        				+ "\"author\" : \"Bob Saget\","
        				+ "\"tag\" : ["
        				+ "\"dairy\","
        				+ "\"vegetarian\""
        				+ "],"
        				+ "\"description\" : \"The simplest breakfast you'll ever make\","
        				+ "\"instructions\" : \"Step 1: Pour cereal in bowl; Step 2: Pour milk in bowl\""
        				+ "\"ingredients\" : ["
        					+ "{"
        					+ "\"name\" : \"Cereal\","
	        				+ "\"tag\" : \"null\","
	        				+ "\"description\" : \"Kellog's Brand\","
	        				+ "\"quantity\" : \"1\","
	        				+ "\"unit\" : \"cup\""
	        				+ "},"
	        				+ "{"
	        				+ "\"name\" : \"Milk\","
	        				+ "\"tag\" : \"dairy\","
	        				+ "\"description\" : \"Kellog's Brand\","
	        				+ "\"quantity\" : \"0.5\","
	        				+ "\"unit\" : \"cup\""
	        				+ "}"
	        				+ "]"
	        				+ "}", Recipe.class);
        	}
        	catch(Exception e){
        		return Response.ok(e.getMessage()).build();
        	}
        	if (r1 != null)
        		return Response.ok(r1.getName()).build();
        	return null;
        }
        
        @GET
        @Path("/test4")
        @Produces(MediaType.APPLICATION_JSON)
        public Response usingParser(
        		@QueryParam("recipeId") String query){
        	MongoDatabase database = mongoClient.getDatabase(db_name);
    		MongoCollection<Document> recipeCollection = database.getCollection(collection_name);
    		
    		Document colQuery = new Document().append("name","CAP");
    				
    		
    		MongoCursor<Document> recipeIterator = recipeCollection.find(colQuery).iterator();
    		
    		//Recipe r1 = RecipeManager.parseRecipe(recipeIterator);
    		
//    		BasicDBList list = new BasicDBList();
//    		while(recipeIterator.hasNext()){
//    			Document doc = recipeIterator.next();
//    			for(String curr : doc.keySet())
//    				doc.get(curr);
//    			//list.add(doc);
//    			//Document current = Document.parse((String) doc.get("ingredients"));
//    			ArrayList<Document> currentIngredients = (ArrayList<Document>) doc.get("ingredients");
//    			for(Document current : currentIngredients)
//    				list.add(current);
//    		}
            	return Response.ok("Hello").build();//r1.getAuthor()).build();//Response.ok(r2.toString()).build();
         }
        
        @GET
        @Path("/test5")
        @Produces(MediaType.TEXT_PLAIN)
        public String usingPOJO(
        		@QueryParam("recipeId") String query){
        	//Get the database we are currently using
        	DB database = mongoClient.getDB(db_name);
        	
        	//Get specifically the recipe collection from the DB
    		DBCollection recipeCollection = database.getCollection(collection_name);
    		
    		//Map the recipe collection with the 
    		JacksonDBCollection<Recipe, String> coll = JacksonDBCollection.wrap(recipeCollection, 
    				Recipe.class, String.class);
    		
    		Recipe recipe = coll.findOne(DBQuery.is("name", "CAP"));
    		//List<Recipe> list = new ArrayList<>();
    		DBCursor<Recipe> rCursor = coll.find();
    		Document doc;
    		//BasicDBList list = new BasicDBList();
    		ArrayList<Recipe> list = new ArrayList<>();
    		while(rCursor.hasNext()){
//    			doc = rCursor.next();
    			list.add(rCursor.next());
    		}
            	return list.get(0).getName();//list.get(0);//r1.getAuthor()).build();//Response.ok(r2.toString()).build();
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
        	String pantry = RecipeManager.getUserPantry("59bae6bc46e0fb00012e87b5");
        	
        	//It is converted into a JSONObject
        	JSONObject pantryJSON = new JSONObject(pantry);
        	
        	//We get the pantry array from the JSONObject
        	JSONArray pantryArray = (JSONArray) pantryJSON.get("pantry");
        	
        	//Variable to iterate
        	JSONObject curr;
        	
        	//ArrayList that will hold user's ingredients
        	ArrayList<String> pantryIngredients = new ArrayList<>();
        	
        	//We Iterate through the pantryJSON to get the ingredient names
        	for(int j = 0; j < pantryArray.length(); j++){
        		
        		//Hold current in a JSONObject variable
        		curr = (JSONObject) pantryArray.get(j);
        		
        		//Store current JSONObject ingredient name in pantryIngredients
        		pantryIngredients.add(curr.getString("ingredient"));
        	}
        	
        	//List to add all of the recipes
        	BasicDBList list = new BasicDBList();
        	
        	//ArrayList and Document variables for next iterations
        	ArrayList<Document> currentIngredients;
        	Document ingredient;
        	
        	//Iteration continues while the iterator still has documents
        	while(recipeIterator.hasNext()){
        		//Holds next document of the current recipe JSONObject
        		Document currentRecipe = recipeIterator.next();
        		
    			//We convert its value into an ArrayList of Documents
    			currentIngredients =  (ArrayList<Document>) currentRecipe.get("ingredients");
    			
    			//We iterate through its JSONObjects
    			for(int i = 0; i < currentIngredients.size(); i++){
        				
    				//We hold the current ingredient in a variable
    				ingredient = currentIngredients.get(i);
    				
    				//Verifies one specific ingredient (used later to validate pantry ingredients)
    				if(pantryIngredients.contains(ingredient.getString("name").toLowerCase())){
    					
	    				//Appends a value that validates if the user has enough ingredients
    					ingredient.append("has", "1");
    					//TODO validate quantity
    				}
    				
    				else{
    					//Ingredient is no inside the users pantry
    					ingredient.append("has", "0");
    				}
    				//Modified ingredient Document is set into the current index in the ArrayList
    				currentIngredients.set(i, ingredient);
    				
        		}
    			//New Modified ArrayList replaces the old ArrayList
    			currentRecipe.replace("ingredients", currentIngredients);
        		
        		//Current modified Recipe is added into the list to return
        		list.add(currentRecipe);
        	}
        	//Modified Recipe List is returned
        	return list.toString();
        }
        
        
        @GET
         @Path("/{recipeId}")
         @Produces(MediaType.APPLICATION_JSON)
         public void getRecipe(@PathParam("recipeId") String query){
                String showRecipeResponse;
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

//       @Produces(MediaType.APPLICATION_JSON)
//       public Recipe getRecipe(@PathParam("recipeId") int id  ){
//               Ingredient i1 = new Ingredient("Milk", "fdsfs12e1", "3 Monjitas", 1, true, "03/11/18", null);
//                      Ingredient i2 = new Ingredient("Cereal", "fopsdf3", "Kellog's", 1, true, "06/10/18", null);
//                      Recipe r = new Recipe("Cereal and Milk", "kmnkkn3l", "The simplest breakfast you'll ever make", 
//                                      "Step 1: Pour cereal in bowl; Step 2: Pour milk in bowl", null);
//                      r.addIngredient(i1);
//                      r.addIngredient(i2);
//                      Ingredient i3 = new Ingredient("Milk", "fdsfs12e1", "Silk", 1, true, "03/11/18", null);
//                      Ingredient i4 = new Ingredient("Cereal", "fopsdf3", "Fruit Loops", 1, true, "06/10/18", null);
//                      Recipe r2 = new Recipe("Cerealy and Milky", "kmnkkn3l", "The simplest breakfast you'll ever make", 
//                                      "Step 1: Pour cereal in bowl; Step 2: Pour milk in bowl", null);
//                      r.addIngredient(i3);
//                      r.addIngredient(i4);
//               ArrayList<Recipe> recipes = new ArrayList<>();
//               recipes.add(r);
//               recipes.add(r2);
//               return recipes.get(id);
//       }
}
