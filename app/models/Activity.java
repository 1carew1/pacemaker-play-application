package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.joda.time.DateTime;

import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;
import com.google.common.base.Objects;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
/**
 * Class used for creating activities of a user
 * @author colmcarew
 *
 */

@SuppressWarnings("serial")
@Entity
@EntityConcurrencyMode(ConcurrencyMode.NONE)
public class Activity extends Model {
	@Id
	@GeneratedValue
	public Long id;
	public String kind;
	public String location;
	@Required
	public double distance;
	@Required
	public String startTime;
	@Required
	public String duration;

	@OneToMany(cascade = CascadeType.ALL)
	public List<Location> routes = new ArrayList<>();
	
	
	/**
	 * Default constructor
	 */
	public Activity() {
	}
	
	/**
	 * Constructor
	 * @param type
	 * @param location
	 * @param distance
	 * @param startTime
	 * @param duration
	 */
	public Activity(String type, String location, double distance, DateTime startTime, String duration) {
		this.kind = type;
		this.location = location;
		this.distance = distance;
		this.startTime = startTime.toDate().toString();
		this.duration = duration;
	}
	
	/**
	 * To String
	 */
	@Override
	public String toString() {
		return "A user created an activity to keep track of their " + kind + " for " + distance + "km";
	}
	
	/**
	 * An activity is equal to another if they are at the same time
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Activity) {
			final Activity other = (Activity) obj;
			return Objects.equal(startTime, other.startTime);
		} else {
			return false;
		}
	}
	
	/**
	 * Has based on the time of the activity
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(this.startTime);
	}
	
	/**
	 * Method used to print the time of an activity
	 * @return
	 */
	public String printTime() {
		String dateTime = "";
		if (startTime != null) {
			dateTime = startTime.toString().replaceAll("GMT\\s+\\d+", "");
		}
		return dateTime;

	}

	/**
	 * Find all activities that have been created
	 * @return
	 */
	public static List<Activity> findAll() {
		return find.all();
	}
	
	/**
	 * Find an activity by its id
	 * @param id
	 * @return
	 */
	public static Activity findById(Long id) {
		return find.where().eq("id", id).findUnique();
	}
	
	/**
	 * Activity Finder
	 */
	public static Model.Finder<String, Activity> find = new Model.Finder<String, Activity>(String.class,
			Activity.class);

}