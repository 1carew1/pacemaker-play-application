package utils;

import java.util.Comparator;

import models.User;
/**
 * Comparator used to sort users by first name
 * @author colmcarew
 *
 */
public class UserFirstNameComparator implements Comparator<User> {
	
	/**
	 * Compare two users based on their first names and sort alphabetically
	 */
	@Override
	public int compare(User user1, User user2) {
		return user1.firstname.compareTo(user2.firstname);
	}
}