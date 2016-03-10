package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;

import play.db.ebean.Model;
/**
 * Class used for storing friends
 * @author colmcarew
 *
 */

@SuppressWarnings("serial")
@Entity
@EntityConcurrencyMode(ConcurrencyMode.NONE)
public class Friends extends Model {
	@Id
	@GeneratedValue
	public Long id;
	public Long userId;
	public Long friendId;
	// made a string instead of boolean due to persistence exception and not saving to DB correctly
	public String accepted = "No";

	/**
	 * default constructor
	 */
	public Friends() {

	}
	
	/**
	 * Constructor
	 * @param userId
	 * @param friendId
	 */
	public Friends(Long userId, Long friendId) {
		this.userId = userId;
		this.friendId = friendId;
	}
	
	/**
	 * Find all Friends entries
	 * @return
	 */
	public static List<Friends> findAll() {
		return find.all();
	}
	
	/**
	 * Get a list of all people that added a user that they haven't accepted
	 * @param userId
	 * @return
	 */
	public static List<Friends> findAllPendingFriendsThatAddedYou(Long userId) {
		return find.where().eq("friendId", userId).eq("accepted", "No").findList();
	}
	
	/**
	 * Get a list of all friends a user added who haven't accepted
	 * @param userId
	 * @return
	 */
	public static List<Friends> findAllPendingFriendsThatYouAdded(Long userId) {
		return find.where().eq("userId", userId).eq("accepted", "No").findList();
	}
	
	/**
	 * List all friends that a user accepted
	 * @param userId
	 * @return
	 */
	public static List<Friends> findAllAcceptedFriends(Long userId) {
		return find.where().eq("userId", userId).eq("accepted", "Yes").findList();
	}
	
	public static List<Friends> findAllFriendsAssociatedWithYou(Long userId) {
		return find.where().eq("userId", userId).findList();
	}

	
	/**
	 * Find a friend by user id and friend id
	 * @param userId
	 * @param friendId
	 * @return
	 */
	public static Friends findById(Long userId, Long friendId) {
		return find.where().eq("friendId", friendId).eq("userId", userId).findUnique();
	}
	
	
	/**
	 * Friends finder
	 */
	public static Model.Finder<String, Friends> find = new Model.Finder<String, Friends>(String.class, Friends.class);
}
