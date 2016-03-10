package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;
import com.google.common.base.Objects;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
/**
 * Class used to store a user
 * @author colmcarew
 *
 */
@SuppressWarnings("serial")
@Entity
@EntityConcurrencyMode(ConcurrencyMode.NONE)
// @Table(name="my_user")
public class User extends Model {
	@Id
	@GeneratedValue
	public Long id;
	public String firstname;
	public String lastname;
	@Required
	public String email;
	@Required
	public String password;
	public boolean isPublicViewable = true;
	public boolean isFriendViewable = true;
	public String profilePhoto = "images/profilePhotos/default.jpg";
	public Date lastLoginDate;

	@OneToMany(cascade = CascadeType.ALL)
	public List<Activity> activities = new ArrayList<Activity>();
	
	/**
	 * default constructor
	 */
	public User() {
	}
	
	/**
	 * Constructor
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @param password
	 */
	public User(String firstname, String lastname, String email, String password) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.password = password;
	}
	
	/**
	 * Update user via user object
	 * @param user
	 */
	public void update(User user) {
		this.firstname = user.firstname;
		this.lastname = user.lastname;
		this.email = user.email;
		this.password = user.password;
	}
	
	/**
	 * To String
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("Id", id).add("Firstname", firstname).add("Lastname", lastname)
				.add("Email", email).add("Passwrod", password).toString();
	}
	
	/**
	 * Two users are equal if their email is equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof User) {
			final User other = (User) obj;
			return Objects.equal(email, other.email);
		} else {
			return false;
		}
	}
	
	/**
	 * Hash based on email
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(this.email);
	}
	
	/**
	 * Find user by their email
	 * @param email
	 * @return
	 */
	public static User findByEmail(String email) {
		return User.find.where().eq("email", email).findUnique();
	}
	
	/**
	 * Find a user by their id
	 * @param id
	 * @return
	 */
	public static User findById(Long id) {
		return find.where().eq("id", id).findUnique();
	}
	
	/**
	 * Find all users
	 * @return
	 */
	public static List<User> findAll() {
		return find.all();
	}
	
	/**
	 * Delete all users
	 */
	public static void deleteAll() {
		for (User user : User.findAll()) {
			user.delete();
		}
	}
	
	/**
	 * User finder
	 */
	public static Model.Finder<String, User> find = new Model.Finder<String, User>(String.class, User.class);

}