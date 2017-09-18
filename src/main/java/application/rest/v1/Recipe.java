package application.rest.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	
	private String name;
	private String id;
	private String _id;
	private String author;
	private List<String> tag;
	private String description;
	private String instructions;
	private List<RecipeIngredient> ingredients;
	
	
	
	public Recipe(String name, String id, String _id, String author, String description, String instructions) {
		this.name = name;
		this.id = id;
		this.author = author;
		this.tag = null;
		this.description = description;
		this.instructions = instructions;
		this.ingredients = null;
	}
	
	public Recipe(){
		name = "";
		id = "";
		author = "";
		tag = null;
		description = "";
		instructions = "";
		ingredients = null;
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
	@JsonProperty("name")
	public String getName() {
		return name;
	}
	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}
	@JsonProperty("id")
	public String getId() {
		return id;
	}
	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}
	@JsonProperty("_id")
	public String get_Id() {
		return _id;
	}
	@JsonProperty("_id")
	public void set_Id(String _id) {
		this._id = _id;
	}
	@JsonProperty("author")
	public String getAuthor() {
		return author;
	}
	@JsonProperty("author")
	public void setAuthor(String author) {
		this.author = author;
	}
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}
	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}
	@JsonProperty("instructions")
	public String getInstructions() {
		return instructions;
	}
	@JsonProperty("instructions")
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	@JsonProperty("ingredients")
	public List<RecipeIngredient> getIngredients() {
		return ingredients;
	}
	@JsonProperty("ingredients")
	public void setIngredients(List<RecipeIngredient> ingredients) {
		this.ingredients = ingredients;
	}
	@JsonProperty("tag")
	public List<String> getTag() {
		return tag;
	}
	@JsonProperty("tag")
	public void setTag(List<String> tag) {
		this.tag = tag;
	}
	
	public void addTag(String tag) {
		this.tag.add(tag);
	}
	public void addIngredient(RecipeIngredient ingredient){
		ingredients.add(ingredient);
	}
}
