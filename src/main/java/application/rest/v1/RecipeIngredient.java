package application.rest.v1;

import java.util.ArrayList;
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
public class RecipeIngredient {
	
	private String name;
	private String tag;
	//private String id; Not sure if ID will be needed here
	private String notes;
	private Double quantity;
	private String unit;
	
	public RecipeIngredient(String name, String tag, String notes, double quantity, String unit) {
		this.name = name;
		this.tag = tag;
		//this.id = id;
		this.notes = notes;
		this.quantity = quantity;
		this.unit = unit;
	}
	
	//Need to verify Objects
	/*public DBObject toDBObject() {
		List<String> tagList = tags;
		BasicDBObject conversion = new BasicDBObject("name", this.getName())
								.append("tags", this.getTags())
								.append("notes", this.getNotes())
								.append("quantity", this.getQuantity());
		return conversion;		
	}*/
	
	//Not complete, maybe needed to add ingredients list in an array
	/*public JSONArray getTagList(){
		List<String> Ltags = tags;
		JSONArray jarray = new JSONArray();
		for(int i = 0; i < tags.size(); i++){
			JSONObject current = new JSONObject();
            try {
                current.put("name", sList.get(index).getName());
                eachData.put("dob", sList.get(index).getDob());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            allDataArray.put(eachData);
        }
		}
	}*/
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	//Not sure if ID will be needed here
	/*public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}*/
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	
	public static ArrayList<RecipeIngredient> parseRecipeIngredient(String ingredients){
		ArrayList<RecipeIngredient> ingredientList = new ArrayList<>();
		ArrayList<String> currIngredient = new ArrayList<>();
		RecipeIngredient ri;
		//Brackets and braces are removed
		ingredients = ingredients.substring(17, ingredients.length()-1);
		ingredients = ingredients.replaceAll("[\\{\\}]", "");
		System.out.println(ingredients);
		
		//Documents inside each ingredient objects are split
		String[] str2 = ingredients.split(",");
		
		//Iterates through all of the available ingredients
		int i = 0;
		while(i < str2.length){
			//Iterates through the current ingredient values
			for(int j = 0; j < 5 && i<str2.length; j++, i++){
			//currIngredient is assigned to one of the ingredient object values
			System.out.println(str2[i].split(":")[1]);
			currIngredient.add(j, str2[i].split(":")[1]); //Keys and values are split
			}
			//Creates a new RecipeIngredient with the current ingredients values
			ri = new RecipeIngredient(currIngredient.get(0), currIngredient.get(1), 
					currIngredient.get(2), Double.parseDouble(currIngredient.get(3).substring(2,3)), currIngredient.get(4));
			//Adds the recipe ingredient to the Recipe Ingredients list
			ingredientList.add(ri);
		}
	
		return ingredientList;
	}

	
	
}