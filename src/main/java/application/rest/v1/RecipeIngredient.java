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
	MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://sapphires:saveoryArmory"
			+ "@sapphires-db.rtp.raleigh.ibm.com:27017/saveory_app"));
	
	private String name;
	private ArrayList<String> tags;
	//private String id; Not sure if ID will be needed here
	private String notes;
	private double quantity;
	public RecipeIngredient(String name, ArrayList<String> tags, String notes, double quantity) {
		this.name = name;
		this.tags = tags;
		//this.id = id;
		this.notes = notes;
		this.quantity = quantity;
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
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setName(ArrayList<String> tags) {
		this.tags = tags;
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
	
	
}