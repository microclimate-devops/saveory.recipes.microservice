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
public class Ingredient {
	//MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://sapphires:saveoryArmory"
	//		+ "@sapphires-db.rtp.raleigh.ibm.com:27017/saveory_app"));

	private String name;
	private String id;
	private String brand;
	private int quantity;
	private boolean opened;
	private String expirationDate;
	private ArrayList<String> tags;
	
	
	
	public Ingredient(String name, String id, String brand, int quantity, boolean opened, String expirationDate,
			ArrayList<String> tags) {
		super();
		this.name = name;
		this.id = id;
		this.brand = brand;
		this.quantity = quantity;
		this.opened = opened;
		this.expirationDate = expirationDate;
		this.tags = tags;
	}
	
	
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
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public boolean isOpened() {
		return opened;
	}
	public void setOpened(boolean opened) {
		this.opened = opened;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	
	
}

