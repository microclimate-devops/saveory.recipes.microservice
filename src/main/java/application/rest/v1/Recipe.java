package application.rest.v1;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
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

@XmlRootElement
public class Recipe {
	MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://sapphires:saveoryArmory"
			+ "@sapphires-db.rtp.raleigh.ibm.com:27017/saveory_app"));

	private String name;
	private String id;
	private String author;
	private ArrayList<String> tags = new ArrayList<String>();
	private String description;
	private String instructions;
	private ArrayList<RecipeIngredient> ingredients = new ArrayList<RecipeIngredient>();
	
	
	
	public Recipe(String name, String id, String author, String description, String instructions) {
		this.name = name;
		this.id = id;
		this.author = author;
		this.description = description;
		this.instructions = instructions;
	}
	
	public Recipe(){
		name = "";
		id = "";
		author = "";
		tags = null;
		description = "";
		instructions = "";
	}
	
	//Need to verify the object types
	/*public DBObject toDBObject(Recipe recipe) {
	    BasicDBObject conversion = new BasicDBObject("name", recipe.getName())
	                     .append("id", recipe.getId())
	                     .append("author", recipe.getAuthor())
	                     .append("description", recipe.getDescription())
	                     .append("instructions", recipe.getInstructions());
	    	for(RecipeIngredient current : ingredients){
	    		conversion.append("ingredients", current.toDBObject());
	    	}
	    return conversion;
	}*/
	
	//public static Recipe toRecipe(Mongo)
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	public ArrayList<RecipeIngredient> getIngredients() {
		return ingredients;
	}
	public void setIngredients(ArrayList<RecipeIngredient> ingredients) {
		this.ingredients = ingredients;
	}
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	public void addTag(String tag) {
		tags.add(tag);
	}
	
	public void addIngredient(RecipeIngredient ingredient){
		ingredients.add(ingredient);
	}
}
