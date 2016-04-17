package controllers;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import models.Activity;
import models.Friends;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import utils.UserFirstNameComparator;
import views.html.*;

/**
 * Controller used to action various user specific items
 * 
 * @author colmcarew
 *
 */

public class Accounts extends Controller {

	public static String defaultImage = "images/profilePhotos/default.jpg";
	private static final Form<User> userForm = Form.form(User.class);
	private static final Form<User> loginForm = Form.form(User.class);

	/**
	 * Main page of application
	 * 
	 * @return
	 */
	public static Result index() {
		List<User> users = User.findAll();
		List<Activity> activities = new ArrayList<>();
		for (User u : users) {
			if (u.isPublicViewable && !u.activities.isEmpty()) {
				activities.add(u.activities.get(u.activities.size() - 1));
			}
		}
		Result toBeReturned = null;
		if (!activities.isEmpty()) {
			toBeReturned = ok(welcome_main.render(activities.get(activities.size() - 1).toString()));
		} else {
			toBeReturned = ok(welcome_main.render("No Activities Yet!"));
		}
		return toBeReturned;
	}

	// For Some Reason I cannot get play to work with java 8
	// public static Result index() {
	// //Filter users who dont want their activities public
	// List<User> users = User.findAll().stream().filter(u ->
	// u.isPublicViewable).collect(Collectors.toList());
	// //Get the most recent activity from each user
	// List<Activity> activities = users.stream()
	// .map(u -> u.activities.get(u.activities.size() - 1))//get latest activity
	// of each user
	// .collect(Collectors.toList());//put it into a list
	// Result toBeReturned = null;
	// if (!activities.isEmpty()) {
	// //Display the last activity in the activities list
	// toBeReturned = ok(welcome_main.render(activities.get(activities.size() -
	// 1).toString()));
	// } else {
	// toBeReturned = ok(welcome_main.render("No Activities Yet!"));
	// }
	// return ok(welcome_main.render("No Activities Yet!"));;
	// }

	/**
	 * Signup page
	 * 
	 * @return
	 */
	public static Result signup() {
		return ok(accounts_signup.render(""));
	}

	/**
	 * Login page
	 * 
	 * @return
	 */
	public static Result login() {
		return ok(accounts_login.render(""));
	}

	/**
	 * This page shows all users present in the application
	 * 
	 * @return
	 */
	public static Result users() {
		List<User> users = User.findAll();
		Collections.sort(users, new UserFirstNameComparator());
		return ok(accounts_users.render(users));
	}

	/**
	 * Get a specific user and displays their info
	 * 
	 * @param id
	 * @return
	 */
	public static Result showUser(Long id) {
		User user = User.findById(id);
		return ok(accounts_showuser.render(user));
	}

	/**
	 * This is the friends page and shows pending friends and current friends
	 * 
	 * @return
	 */
	public static Result friends() {
		User user = User.findByEmail(session().get("email"));
		List<User> acceptedFriends = new ArrayList<>();
		List<User> pendingFriends = new ArrayList<>();
		List<User> pendingFriendsYouAdded = new ArrayList<>();

		for (Friends f : Friends.findAllAcceptedFriends(user.id)) {
			acceptedFriends.add(User.findById(f.friendId));
		}
		for (Friends f : Friends.findAllPendingFriendsThatYouAdded(user.id)) {
			pendingFriendsYouAdded.add(User.findById(f.friendId));
		}
		for (Friends f : Friends.findAllPendingFriendsThatAddedYou(user.id)) {
			pendingFriends.add(User.findById(f.userId));
		}
		Collections.sort(acceptedFriends, new UserFirstNameComparator());
		Collections.sort(pendingFriends, new UserFirstNameComparator());
		Collections.sort(pendingFriendsYouAdded, new UserFirstNameComparator());
		pendingFriends.addAll(pendingFriendsYouAdded);

		return ok(accounts_showfriends.render(pendingFriends, acceptedFriends));
	}

	/**
	 * This is a logout method which redirects to the main page
	 * 
	 * @return
	 */
	public static Result logout() {
		session().clear();
		return redirect(routes.Accounts.index());
	}

	/**
	 * This is called from the signup page to create a user
	 * 
	 * @return
	 */
	public static Result register() {
		Result result = null;
		Form<User> boundForm = userForm.bindFromRequest();
		if (boundForm.hasErrors()) {
			result = badRequest(accounts_signup.render("Please fill in all the details correctly"));
		} else {
			User userToCheck = boundForm.get();
			boolean alreadyThere = utils.UserUtils.checkForUser(userToCheck);
			if (!alreadyThere) {
				User user = boundForm.get();
				Logger.info("User = " + user.toString());
				user.save();
				Logger.info(new Date() + " User " + user.firstname + " " + user.lastname + " has been created");
				result = ok(accounts_login.render(""));
			} else {
				result = ok(accounts_signup.render("A user with the email " + userToCheck.email + " already exists"));
			}
		}
		return result;
	}

	/**
	 * This is called from the sign in page to authenticate the user and
	 * redirects to the dashboard
	 * 
	 * @return
	 */
	public static Result authenticate() {
		Result result = null;
		Form<User> boundForm = loginForm.bindFromRequest();
		if (loginForm.hasErrors() || boundForm.hasErrors()) {
			Logger.info(new Date() + " Login form has errors");
			result = badRequest(accounts_login.render(""));
		} else {
			User user = boundForm.get();
			if (utils.UserUtils.checkForUser(user)) {
				User dbUser = User.findByEmail(user.email);
				if (dbUser.password.equals(user.password)) {
					session("email", boundForm.get().email);
					dbUser.lastLoginDate = new Date();
					Logger.info(new Date() + " " + dbUser.firstname + " " + dbUser.lastname + " has logged in");
					dbUser.save();
					result = redirect(routes.Dashboard.index());
				} else {
					Logger.info(new Date() + " Wrong password enetered for " + user.email);
					result = ok(accounts_login.render("Wrong Password"));
				}

			} else {
				Logger.info(new Date() + " Invalid login");
				result = ok(accounts_login.render("Wrong Email"));
			}
		}
		return result;
	}

	/**
	 * This is a form for editing a user info
	 * 
	 * @return
	 */
	public static Result editUserForm() {
		return ok(accounts_edituserinfo.render(""));
	}

	/**
	 * This method is called from the form to commit the user changes
	 * 
	 * @return
	 */
	public static Result editUser() {
		User u = User.findByEmail(session().get("email"));
		List<User> users = User.findAll();
		List<String> emails = new ArrayList<>();
		for (User us : users) {
			emails.add(us.email);
		}
		DynamicForm requestData = Form.form().bindFromRequest();
		String firstname = requestData.get("firstname");
		String lastname = requestData.get("lastname");
		String email = requestData.get("email");

		u.firstname = (firstname != null && !firstname.equals("")) ? firstname : u.firstname;
		u.lastname = (lastname != null & !lastname.equals("")) ? lastname : u.lastname;
		if (email != null && !emails.contains(email) && !email.equals("")) {
			Logger.info(new Date() + " Changing " + u.firstname + " " + u.lastname + "'s emails");
			u.email = email;
			session().clear();
		} else {
			Logger.info(new Date() + " Problem changing email for " + u.firstname + " " + u.lastname
					+ " as email may already exist or new email is blank");
		}
		Logger.info(new Date() + " Updated " + u.firstname + " " + u.lastname + "'s user info");
		u.save();

		return redirect(routes.Accounts.index());
	}

	/**
	 * This method is used to delete a user and redirects to the main page of
	 * the application
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	public static Result deleteUser() {
		User u = User.findByEmail(session().get("email"));
		if (u != null) {
			session().clear();
			Logger.info(new Date() + " Deleting User: " + u.firstname + " " + u.lastname);
			List<Friends> addedMe = Friends.findAllFriendsAssociatedWithYou(u.id);
			List<Long> friendsIds = new ArrayList<>();
			for (Friends f : addedMe) {
				friendsIds.add(f.friendId);
			}
			for (Long f : friendsIds) {
				utils.UserUtils.unfriend(u.id, f);
			}
			utils.UserUtils.deletePhoto(u.profilePhoto);

			u.delete();
		} else {
			Logger.info(new Date() + " User is null cannot delete");
		}
		return redirect(routes.Accounts.index());
	}

	/**
	 * This method is used to change the password of a user
	 * 
	 * @return
	 */
	public static Result changePassword() {
		Result result = null;
		User user = User.findByEmail(session().get("email"));
		if (user != null) {
			DynamicForm requestData = Form.form().bindFromRequest();
			String oldpassword = requestData.get("oldpassword");
			String newpassword = requestData.get("newpassword");
			String newpassword2 = requestData.get("newpassword2");

			if (user.password.equals(oldpassword)) {
				if (newpassword.equals(newpassword2) && !newpassword.equals("") && !newpassword2.equals("")) {
					user.password = newpassword;
					user.save();
					result = ok(dashboard_settings.render("Password has been changed"));
				} else {
					Logger.info(new Date() + " User: " + user.firstname + " has not entered the same new password");
					result = badRequest(dashboard_settings.render("New passwords do not match"));
				}
			} else {
				Logger.info(new Date() + " User: " + user.firstname + " has entered the wrong password");
				result = badRequest(dashboard_settings.render("Incorrect password"));
			}
		} else {
			Logger.info(new Date() + " User is null");
			result = redirect(routes.Accounts.index());
		}
		return result;
	}

	/**
	 * This method is used to upload a profile photo for a user
	 * 
	 * @return
	 */
	public static Result upload() {
		Result result = null;
		User user = User.findByEmail(session().get("email"));
		MultipartFormData body = request().body().asMultipartFormData();
		Logger.info(new Date() + " Getting new profile photo");
		FilePart picture = body.getFile("picture");
		if (picture != null) {
			String fileName = picture.getFilename();
			if (fileName.matches(".+?\\.(png|jpg|jpeg|gif)")) {
				utils.UserUtils.uploadPictue(user, fileName, picture);
				result = redirect(routes.Dashboard.userSettings());
			} else {
				Logger.info(new Date() + " File format not supported");
				result = ok(dashboard_settings.render("This type of image is not supported"));
			}

		} else {
			Logger.info(new Date() + " Picture was null");
			result = redirect(routes.Dashboard.userSettings());
		}
		return result;
	}

	/**
	 * This method deletes the profile photo of a user
	 * 
	 * @return
	 */
	public static Result deleteProfilePhoto() {
		Result result = null;
		User user = User.findByEmail(session().get("email"));
		if (user == null) {
			Logger.info(new Date() + " Cannot delete profile photo of null user");
			result = ok(dashboard_settings.render("This type of image is not supported"));
		} else {
			String oldProfilePhoto = user.profilePhoto;
			utils.UserUtils.deletePhoto(oldProfilePhoto);
			user.profilePhoto = defaultImage;
			Logger.info(new Date() + " Deleting profile photo of " + user.firstname + " " + user.lastname);
			user.save();
			result = redirect(routes.Dashboard.userSettings());
		}
		return result;
	}

	/**
	 * Form/button to add a friend
	 * 
	 * @param friendId
	 * @return
	 */
	public static Result addFriend(Long friendId) {
		User user = User.findByEmail(session().get("email"));
		utils.UserUtils.addFriend(user.id, friendId);
		return redirect(routes.Accounts.users());
	}

	/**
	 * Form/button to accept a friend
	 * 
	 * @param friendId
	 * @return
	 */
	public static Result acceptFriend(Long friendId) {
		User user = User.findByEmail(session().get("email"));
		utils.UserUtils.acceptFriend(user.id, friendId);
		return redirect(routes.Accounts.friends());
	}

	/**
	 * Form/button to unfriend someone
	 * 
	 * @param friendId
	 * @return
	 */
	public static Result unfriend(Long friendId) {
		User user = User.findByEmail(session().get("email"));
		utils.UserUtils.unfriend(user.id, friendId);
		return redirect(routes.Accounts.friends());
	}

	/**
	 * For used to search for a user and return the list
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Result searchUser() throws SQLException {
		DynamicForm requestData = Form.form().bindFromRequest();
		String searchString = requestData.get("username");
		List<User> users = utils.UserUtils.searchForUsersByName(searchString);
		return ok(accounts_users.render(users));
	}

	/**
	 * Method used to return a image to the user
	 * 
	 * @param filename
	 * @return
	 */
	public static Result getImage(String filename) {
		Result result = null;
		File file = utils.UserUtils.getImage(filename);
		if (file.exists()) {
			result = ok(file);
		} else {
			result = badRequest();
		}
		return result;
	}
	
	/**
	 * Method used to return a image to the user
	 * 
	 * @param filename
	 * @return
	 */
	public static Result getImageWithImageNameOnly(String filename) {
		Result result = null;
		File file = utils.UserUtils.getImageWithImageNameOnly(filename);
		if (file.exists()) {
			result = ok(file);
		} else {
			result = badRequest();
		}
		return result;
	}
	
	
	/**
	 * This method is used to upload a profile photo for a user via REST
	 * 
	 * @return
	 */
	public static Result uploadUserProfilePhotoViaPost(Long userId) {
		Result result = ok();
		User user = User.findById(userId);
		MultipartFormData body = request().body().asMultipartFormData();
		String isThereAnything = request().remoteAddress();
		Logger.info(new Date() + " Getting new profile photo");
		Logger.info(new Date() + " " + isThereAnything);
//		FilePart picture = body.getFile("picture");
//		if (picture != null) {
//			String fileName = picture.getFilename();
//			if (fileName.matches(".+?\\.(png|jpg|jpeg|gif)")) {
//				utils.UserUtils.uploadPictue(user, fileName, picture);
//				result = redirect(routes.Dashboard.userSettings());
//			} else {
//				Logger.info(new Date() + " File format not supported");
//				result = ok(dashboard_settings.render("This type of image is not supported"));
//			}
//
//		} else {
//			Logger.info(new Date() + " Picture was null");
//			result = redirect(routes.Dashboard.userSettings());
//		}
		return result;
	}
}
