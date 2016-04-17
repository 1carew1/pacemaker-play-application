package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import models.Friends;
import models.User;
import play.Logger;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData.FilePart;

public class UserUtils {

	// TODO Alaways remember to change largePath string in when changing to AWS

	// Change largepath string depending on environment
	// public static String largepath =
	// "/Users/colmcarew/Documents/workspace/pacemaker-web-app-play/public/";
	// public static String largepath =
	// "/home/ubuntu/playprojects/pacemaker-web-app-play/public/";
	// public static String largepath =
	// "/home/colm/Documents/pacemaker-web-app-play/public/";
	public static String largepath = "/home/appfiles/pacemaker/";

	/**
	 * Method to delete a photo given its relative location
	 * 
	 * @param oldProfilePhoto
	 */
	public static void deletePhoto(String oldProfilePhoto) {
		if (!oldProfilePhoto.equals("images/profilePhotos/default.jpg")) {
			File oldPicture = new File(largepath + oldProfilePhoto);
			oldPicture.delete();
		} else {
			Logger.info(new Date() + " Delete failed - cannot delete default photo");
		}
	}

	/**
	 * Method to search for users given their name or email
	 * 
	 * @param searchString
	 * @return
	 */
	public static List<User> searchForUsersByName(String searchString) {
		List<User> users = new ArrayList<>();
		String query = "";
		if (searchString.matches("\\w+\\s+\\w+")) {
			String[] names = searchString.split(" ");
			query = "SELECT id from pacemaker.user where firstname like '%" + names[0] + "%' and lastname like '%"
					+ names[1] + "%';";
		} else {
			query = "SELECT id from pacemaker.user where firstname like '%" + searchString + "%' OR lastname like '%"
					+ searchString + "%' OR email like '%" + searchString + "%';";
		}
		MySQLConnection connection = new MySQLConnection();
		List<Long> ids = connection.getUserIdsFromMysqlQuery(query);
		for (Long l : ids) {
			users.add(User.findById(l));
		}
		Collections.sort(users, new UserFirstNameComparator());
		return users;
	}

	/**
	 * Method to unfriend a friend given the user's id and the friend they wish
	 * to remove
	 * 
	 * @param userId
	 * @param friendId
	 */
	public static String unfriend(Long userId, Long friendId) {
		Friends yourFriendEntry = Friends.findById(userId, friendId);
		Friends theirFriendEntry = Friends.findById(friendId, userId);
		String result = "Not Deleted";
		if (yourFriendEntry != null && theirFriendEntry != null && yourFriendEntry.accepted.equals("Yes")) {
			yourFriendEntry.delete();
			theirFriendEntry.delete();
			result = "Deleted";
		}
		return result;
	}

	/**
	 * Method to accept a friend given the user's id and the firend's id
	 * 
	 * @param userId
	 * @param friendId
	 */
	public static Friends acceptFriend(Long userId, Long friendId) {
		Friends alreadyThere = Friends.findById(friendId, userId);
		alreadyThere.accepted = "Yes";
		Friends f = new Friends(userId, friendId);
		f.accepted = "Yes";
		f.save();
		alreadyThere.save();
		return f;
	}

	/**
	 * Method to request a friend given the user's id and the friend's id
	 * 
	 * @param userId
	 * @param friendId
	 */
	public static Friends addFriend(Long userId, Long friendId) {
		Friends alreadyThere = Friends.findById(userId, friendId);
		Friends f = null;
		if (alreadyThere == null) {
			f = new Friends(userId, friendId);
			f.save();
		}
		return f;

	}

	/**
	 * method to upload a profile photo of a user
	 * 
	 * @param user
	 * @param fileName
	 * @param picture
	 */
	public static void uploadPictue(User user, String fileName, FilePart picture) {
		String oldProfilePhoto = user.profilePhoto;
		fileName = user.firstname + user.lastname + user.id + "_profilephoto_"
				+ fileName.replace(".+?\\.(png|jpg|jpeg|gif)", "\\1");
		fileName = fileName.replaceAll("\\s+", "");
		File file = picture.getFile();
		Logger.info(new Date() + " deleting " + user.firstname + " " + user.lastname + "'s old profile photo");
		deletePhoto(oldProfilePhoto);
		Logger.info(new Date() + " uploading new profile photo");
		file.renameTo(new File(largepath + "images/profilePhotos/", fileName));
		Logger.info(new Date() + " uploading to " + file.getPath());
		user.profilePhoto = "images/profilePhotos/" + fileName;
		Logger.info(new Date() + " Setting " + user.firstname + " " + user.lastname + "'s new profile photo");
		user.save();
	}

	/**
	 * Check if a user already exists
	 * 
	 * @param userToCheck
	 * @return
	 */
	public static boolean checkForUser(User userToCheck) {
		boolean result = false;
		List<User> users = User.findAll();
		for (User u : users) {
			if (u.email.equals(userToCheck.email)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Method used to get an image file
	 * 
	 * @param filename
	 * @return
	 */
	public static File getImage(String filename) {
		File file = new File(largepath + filename);
		if (!file.exists()) {
			file = new File(largepath + "images/profilePhotos/default.jpg");
		}
		return file;
	}
	
	/**
	 * Method used to get an image file
	 * 
	 * @param filename
	 * @return
	 */
	public static File getImageWithImageNameOnly(String filename) {
		File file = new File(largepath + "images/profilePhotos/" + filename);
		if (!file.exists()) {
			file = new File(largepath + "images/profilePhotos/default.jpg");
		}
		return file;
	}

	/**
	 * Method used to get all friends
	 * 
	 * @param filename
	 * @return
	 */
	public static List<Friends> getFriends(Long userId) {
		List<Friends> friends = new ArrayList<>();
		friends.addAll(Friends.findAllPendingFriendsThatYouAdded(userId));
		friends.addAll(Friends.findAllPendingFriendsThatAddedYou(userId));
		friends.addAll(Friends.findAllAcceptedFriends(userId));
		return friends;
	}
}
