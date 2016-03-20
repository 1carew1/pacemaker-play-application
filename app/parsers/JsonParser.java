package parsers;

import java.util.ArrayList;
import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import models.Activity;
import models.Location;
import models.User;
/**
 * Class used to generate and parse JSON strings and objects
 * @author colmcarew
 *
 */
public class JsonParser {
	private static JSONSerializer userSerializer = new JSONSerializer().exclude("class");
	private static JSONSerializer activitySerializer = new JSONSerializer().exclude("class");
	private static JSONSerializer locationSerializer = new JSONSerializer().exclude("class");
	private static JSONSerializer friendsSerializer = new JSONSerializer().exclude("class");
	
	/**
	 * Return a user from a json string
	 * @param json
	 * @return
	 */
	public static User renderUser(String json) {
		return new JSONDeserializer<User>().deserialize(json, User.class);
	}
	
	/**
	 * Create a json string from a user object
	 * @param obj
	 * @return
	 */
	public static String renderUser(Object obj) {
		return userSerializer.serialize(obj);
	}
	
	/**
	 * Create a list of users from a json string
	 * @param json
	 * @return
	 */
	public static List<User> renderUsers(String json) {
		return new JSONDeserializer<ArrayList<User>>().use("values", User.class).deserialize(json);
	}
	
	/**
	 * Create an activity from a json string
	 * @param json
	 * @return
	 */
	public static Activity renderActivity(String json) {
		Activity activity = new JSONDeserializer<Activity>().deserialize(json, Activity.class);
		return activity;
	}
	
	/**
	 * Generate a json string from an Activity object
	 * @param obj
	 * @return
	 */
	public static String renderActivity(Object obj) {
		return activitySerializer.serialize(obj);
	}
	
	/**
	 * Generate a list of Activities from a json string
	 * @param json
	 * @return
	 */
	public static List<Activity> renderActivities(String json) {
		return new JSONDeserializer<ArrayList<Activity>>().use("values", Activity.class).deserialize(json);
	}
	
	/**
	 * Create a Location object from a json string
	 * @param json
	 * @return
	 */
	public static Location renderLocation(String json) {
		Location location = new JSONDeserializer<Location>().deserialize(json, Location.class);
		return location;
	}
	
	/**
	 * Generate json string from a Location object
	 * @param obj
	 * @return
	 */
	public static String renderLocation(Object obj) {
		return locationSerializer.serialize(obj);
	}
	
	
	/**
	 * Create a list of Location objects from a json string
	 * @param json
	 * @return
	 */
	public static List<Location> renderLocations(String json) {
		return new JSONDeserializer<ArrayList<Location>>().use("values", Location.class).deserialize(json);
	}
	
	/**
	 * Generate json string from a list of users object
	 * @param obj
	 * @return
	 */
	public static String renderFriends(Object obj) {
		return friendsSerializer.serialize(obj);
	}
	
	/**
	 * Generate json string from a list a friend
	 * @param obj
	 * @return
	 */
	public static String renderFriend(Object obj) {
		return friendsSerializer.serialize(obj);
	}
}