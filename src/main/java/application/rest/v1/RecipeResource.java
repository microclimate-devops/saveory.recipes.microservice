package application.rest.v1;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
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

@Path("recipes")
public class RecipeResource {
		private static String db_name = "saveory_app";
		private static String db_client_uri = "mongodb://sapphires:saveoryArmory@sapphires-db.rtp.raleigh.ibm.com/saveory_app";
		private static String collection_name = "recipes";
		MongoClient mongoClient = new MongoClient(new MongoClientURI(db_client_uri));
        HttpClient httpclient = HttpClientBuilder.create().build();

        @GET
         @Produces(MediaType.APPLICATION_JSON)
         public String getRecipes(){
        		MongoDatabase database = mongoClient.getDatabase(db_name);
        		MongoCollection<Document> collection = database.getCollection(collection_name);
        		MongoCursor<Document> iterator = collection.find().iterator();
        		
        		BasicDBList list = new BasicDBList();
        		while(iterator.hasNext()){
        			Document doc = iterator.next();
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
