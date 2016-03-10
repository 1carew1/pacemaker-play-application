package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Class used to store a location of an activity
 * @author colmcarew
 *
 */
@SuppressWarnings("serial")
@Entity
@EntityConcurrencyMode(ConcurrencyMode.NONE)
public class Location extends Model {
	@Id
	public Long id;
	@Required
	public float latitude;
	@Required
	public float longitude;

	/**
	 * Default Location Constructor
	 */
	public Location() {
	}

	/**
	 * Location constructor with parameters
	 *
	 * @param latitude
	 * @param longitude
	 */
	public Location(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	// Overridden methods

	/**
	 * To string returns [lat, lng] of Location Object
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "[" + this.latitude + ", " + this.longitude + "]";
	}

	/**
	 * Two Locations are equal is their lattitude and longitude are the same
	 *
	 * @param o
	 * @return
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Location location = (Location) o;

		if (Float.compare(location.latitude, latitude) != 0)
			return false;
		return Float.compare(location.longitude, longitude) == 0;

	}
	
	/**
	 * Hash based on latitude and longitude
	 */
	@Override
	public int hashCode() {
		int result = (latitude != +0.0f ? Float.floatToIntBits(latitude) : 0);
		result = 31 * result + (longitude != +0.0f ? Float.floatToIntBits(longitude) : 0);
		return result;
	}
	
	/**
	 * Find a location by its id
	 * @param id
	 * @return
	 */
	public static Location findById(Long id) {
		return find.where().eq("id", id).findUnique();
	}
	
	/**
	 * Location finder
	 */
	public static Model.Finder<String, Location> find = new Model.Finder<String, Location>(String.class,
			Location.class);
}