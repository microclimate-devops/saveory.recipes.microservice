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
import javax.json.JsonArray;
import javax.json.JsonReader;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
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
import java.io.StringReader;

@Path("recipes")
public class RecipeResource {
		private static String db_name = "saveory_app";
		private static String db_client_uri = "mongodb://sapphires:saveoryArmory@sapphires-db.rtp.raleigh.ibm.com/saveory_app";
		private static String collection_name = "recipes";
		private static String auxCollection = "users";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(db_client_uri));
        

        @GET
         @Produces(MediaType.APPLICATION_JSON)
         public String getRecipes(
        			//@QueryParam("username") String username
        			){
        		MongoDatabase database = mongoClient.getDatabase(db_name);
        		MongoCollection<Document> recipeCollection = database.getCollection(collection_name);
        		MongoCollection<Document> userCollection = database.getCollection(auxCollection);
        		
        		//JSONArray recipe = rCollection.getJsonObject("ingredients":[])
        		
        		MongoCursor<Document> recipeIterator = recipeCollection.find().iterator();
        		MongoCursor<Document> userIterator = userCollection.find().iterator();
        		
        		BasicDBList list = new BasicDBList();
        		while(recipeIterator.hasNext()){
        			Document doc = recipeIterator.next();
        			list.add(doc);
        		}
        		return JSON.serialize(list);
        		
                /*Ingredient i1 = new Ingredient("Milk", "fdsfs12e1", "3 Monjitas", 1, true, "03/11/18", null);
                Ingredient i2 = new Ingredient("Cereal", "fopsdf3", "Kellog's", 1, true, "06/10/18", null);
                Recipe r = new Recipe("Cereal and Milk", "kmnkkn3l", "The simplest breakfast you'll ever make",
                                "Step 1: Pour cereal in bowl; Step 2: Pour milk in bowl", null);
                r.addIngredient(i1);
                r.addIngredient(i2);
                return r;*/
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
        	
        	//Gets the user pantry ingredients
        	String pantry = RecipeManager.getUserPantry(username); //JSONObject.parse(RecipeManager.getUserPantry(username));
        	pantry = pantry.substring(1, pantry.length() - 1);
        	//Read in the requested pantry as a JSON
        	JSONObject pantryJSON = new JSONObject(pantry);
        	JSONArray pantryArray = pantryJSON.getJSONArray("pantry");
        	
        	
        	
//            JsonReader reader = Json.createReader(new StringReader(pantry));
//            JsonObject pantryJSON = reader.readObject();
//            reader.close();
            
            //pantryJSON ***LAST LINE - IN WORK
        	
        	
//        	JSONArray pantryArray = pantry.getJSONArray("ingredients");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject explrObject = jsonArray.getJSONObject(i);
        //}
        	
//        	BasicDBList list = new BasicDBList();
//        	//Map pantryM = pantry.toMap();
//    		for(String current : pantryM.keySet()){
//    			//Document doc = current.next();
//    			list.add((BasicDBObject) pantryM.get((String) current));
//    			//Document current = Document.parse((String) doc.get("ingredients"));
//    			//ArrayList<Document> currentIngredients = (ArrayList<Document>) doc.get("ingredients");
//    			//for(Document current : currentIngredients)
//    			//	list.add(current);
//    		}
        	
        	
        	//XXFor now (no error)
        	return Response.ok(pantryArray.toString()).build(); //JSONObject.serialize(pantry);
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
