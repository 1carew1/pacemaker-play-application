package controllers;

import static parsers.JsonParser.renderActivity;
import static parsers.JsonParser.renderLocation;
import static parsers.JsonParser.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Activity;
import models.Friends;
import models.Location;
import models.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import utils.UserUtils;
/**
 * Controller used for mostly interacting with the CLI tool 
 * @author colmcarew
 *
 */
public class PacemakerAPI extends Controller {

	/**
	 * Get a list of all users
	 * 
	 * @return
	 */
	public static Result users() {
		List<User> users = User.findAll();
		return ok(renderUser(users));
	}

	/**
	 * Get a user
	 * 
	 * @param id
	 * @return
	 */
	public static Result user(Long id) {
		User user = User.findById(id);
		return user == null ? notFound() : ok(renderUser(user));
	}

	/**
	 * Create a user
	 * 
	 * @return
	 */
	public static Result createUser() {
		User user = renderUser(request().body().asJson().toString());
		if (utils.UserUtils.checkForUser(user)) {
			Logger.info(new Date() + " This user already exists");
		} else {
			user.save();
			Logger.info(new Date() + " User: " + user.firstname + " " + user.lastname + " was created");
		}
		return ok(renderUser(user));
	}

	/**
	 * Delete a user
	 * 
	 * @param id
	 * @return
	 */
	public static Result deleteUser(Long id) {
		Result result = notFound();
		User user = User.findById(id);
		if (user != null) {
			Logger.info(new Date() + " Deleting " + user.firstname + " " + user.lastname);
			user.delete();
			result = ok();
		} else {
			Logger.info(new Date() + " This user does not exist");
		}
		return result;
	}

	/**
	 * Delete all users
	 * 
	 * @return
	 */
	public static Result deleteAllUsers() {
		User.deleteAll();
		Logger.info(new Date() + " Deleting all users");
		return ok();
	}

	/**
	 * Update a user
	 * 
	 * @param id
	 * @return
	 */
	public static Result updateUser(Long id) {
		Result result = notFound();
		User user = User.findById(id);
		if (user != null) {
			User updatedUser = renderUser(request().body().asJson().toString());
			user.update(updatedUser);
			user.save();
			result = ok(renderUser(user));
			Logger.info(new Date() + " Updated " + user.firstname + " " + user.lastname + "'s info");
		} else {
			Logger.info(new Date() + " This user does not exist - cannot update");
		}
		return result;
	}

	/**
	 * Get all activities of a user
	 * 
	 * @param userId
	 * @return
	 */
	public static Result activities(Long userId) {
		User u = User.findById(userId);
		List<Activity> activities = new ArrayList<>();
		if (u != null) {
			activities.addAll(u.activities);
		} else {
			Logger.info(new Date() + " This user does not exist so cannot get their activities");
		}
		return ok(renderActivity(activities));
	}

	/**
	 * Add an activity to a user
	 * 
	 * @param userId
	 * @return
	 */
	public static Result createActivity(Long userId) {
		User user = User.findById(userId);
		Activity activity = new Activity();
		if (user != null) {
			// TODO - Delete the println
			// This is a tempeorary patch as date seems to be a problem
//			String json = request().body().asJson().toString();
//			Pattern pattern = Pattern.compile("distance\"\\:.*?(,|})");
//			Matcher matcher = pattern.matcher(json);
//			if (matcher.find()) {
//				activity.distance = Double.parseDouble((matcher.group(0)).replaceAll("distance\"\\:", ""));
//				System.out.println(activity.distance);
//			}
//			pattern = Pattern.compile("duration\"\\:.*?(,|})");
//			matcher = pattern.matcher(json);
//			if (matcher.find()) {
//				activity.duration = (matcher.group(0)).replaceAll("duration\"\\:", "");
//				System.out.println(activity.duration);
//			}
			activity = renderActivity(request().body().asJson().toString());
			user.activities.add(activity);
			Logger.info(new Date() + " Activity added to " + user.firstname + " " + user.lastname);
			user.save();
		} else {
			Logger.info(new Date() + " This user does not exist cannot add activity");
		}
		return ok(renderActivity(activity));
	}

	/**
	 * Get an activity of a user
	 * 
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public static Result activity(Long userId, Long activityId) {
		Result result = null;
		User user = User.findById(userId);
		Activity activity = Activity.findById(activityId);
		if (activity == null || user == null) {
			Logger.info(new Date() + " This user does not exist or the activity does not exist");
			result = notFound();
		} else {
			result = user.activities.contains(activity) ? ok(renderActivity(activity)) : badRequest();
		}
		return result;
	}

	/**
	 * Delete an activity of a user
	 * 
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public static Result deleteActivity(Long userId, Long activityId) {
		Result result = null;
		User user = User.findById(userId);
		Activity activity = Activity.findById(activityId);
		if (activity == null || user == null) {
			Logger.info(new Date() + " This user does not exist or the activity does not exist");
			result = notFound();
		} else {
			if (user.activities.contains(activity)) {
				user.activities.remove(activity);
				activity.delete();
				user.save();
				Logger.info(new Date() + " User " + user.firstname + " " + user.lastname + " deleted an activity");
				result = ok();
			} else {
				result = badRequest();
			}
		}
		return result;
	}

	/**
	 * Update an activity of a user
	 * 
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public static Result updateActivity(Long userId, Long activityId) {
		Result result = null;
		User user = User.findById(userId);
		Activity activity = Activity.findById(activityId);
		if (activity == null || user == null) {
			Logger.info(new Date() + " This user does not exist or the activity does not exist");
			result = notFound();
		} else {
			if (user.activities.contains(activity)) {
				Activity updatedActivity = renderActivity(request().body().asJson().toString());
				Logger.info(new Date() + updatedActivity.toString());
				activity.distance = updatedActivity.distance;
				activity.location = updatedActivity.location;
				activity.kind = updatedActivity.kind;
				activity.startTime = updatedActivity.startTime;
				activity.duration = updatedActivity.duration;
				Logger.info(new Date() + " User " + user.firstname + " " + user.lastname + " updated an activity");
				activity.save();
				result = ok(renderActivity(updatedActivity));
			} else {
				result = badRequest();
			}
		}
		return result;
	}

	/**
	 * Add location to an activity
	 * 
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public static Result createLocation(Long userId, Long activityId) {
		Result result = null;
		User user = User.findById(userId);
		Activity activity = Activity.findById(activityId);
		Location location = new Location();
		if (user == null || activity == null) {
			Logger.info(new Date() + " This user does not exist or the activity does not exist");
			result = notFound();
		} else {
			location = renderLocation(request().body().asJson().toString());
			activity.routes.add(location);
			activity.save();
			Logger.info(
					new Date() + " " + user.firstname + " " + user.lastname + " added a location to their activity");
			result = ok(renderActivity(location));
		}
		return result;

	}

	/**
	 * Get a location of an activity by the id
	 * 
	 * @param userId
	 * @param activityId
	 * @param locationId
	 * @return
	 */
	public static Result location(Long userId, Long activityId, Long locationId) {
		Result result = null;
		User user = User.findById(userId);
		Activity activity = Activity.findById(activityId);
		Location location = Location.findById(locationId);
		if (location == null || activity == null || user == null) {
			Logger.info(new Date()
					+ " This user does not exist or the activity does not exist or this location does not exist");
			result = notFound();

		} else {
			result = activity.routes.contains(location) ? ok(renderActivity(location)) : badRequest();
		}
		return result;
	}

	/**
	 * Delete the location of an activity
	 * 
	 * @param userId
	 * @param activityId
	 * @param locationId
	 * @return
	 */
	public static Result deleteLocation(Long userId, Long activityId, Long locationId) {
		Result result = null;
		User user = User.findById(userId);
		Activity activity = Activity.findById(activityId);
		Location location = Location.findById(locationId);
		if (location == null || activity == null || user == null) {
			Logger.info(new Date()
					+ " This user does not exist or the activity does not exist or this location does not exist");
			result = notFound();
		} else {
			if (activity.routes.contains(location)) {
				activity.routes.remove(location);
				location.delete();
				user.save();
				Logger.info(new Date() + " Location deleted");
				result = ok();
			} else {
				result = badRequest();
			}

		}
		return result;
	}

	/**
	 * Method to update the location of an activity
	 * 
	 * @param userId
	 * @param activityId
	 * @param locationId
	 * @return
	 */
	public static Result updateLocation(Long userId, Long activityId, Long locationId) {
		Result result = null;
		User user = User.findById(userId);
		Activity activity = Activity.findById(activityId);
		Location location = Location.findById(locationId);
		if (location == null || user == null || activity == null) {
			Logger.info(new Date()
					+ " This user does not exist or the activity does not exist or this location does not exist");
			result = notFound();
		} else {
			if (activity.routes.contains(location)) {
				Location updateLocation = renderLocation(request().body().asJson().toString());
				location.latitude = updateLocation.latitude;
				location.longitude = updateLocation.longitude;
				Logger.info(new Date() + " Location Updated");
				location.save();
				result = ok(renderActivity(updateLocation));
			} else {
				result = badRequest();
			}
		}
		return result;
	}

	/**
	 * Method to get all locations of a user's activity
	 * 
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public static Result locations(Long userId, Long activityId) {
		User user = User.findById(userId);
		Activity activity = Activity.findById(activityId);
		List<Location> routes = new ArrayList<>();
		if (user != null && activity != null) {
			routes.addAll(activity.routes);
		}
		return ok(renderLocation(routes));
	}
	
	/**
	 * Method to get all friends
	 * 
	 * @param userId
	 * @return
	 */
	public static Result getFriends(Long userId) {
		List<Friends> friends =  UserUtils.getFriends(userId);
		return ok(renderFriend(friends));
	}
	
	/**
	 * Method to add a friend
	 * 
	 * @param userId
	 * @return
	 */
	public static Result addFriend(Long userId, Long friendId) {
		Friends f = utils.UserUtils.addFriend(userId, friendId);
		return ok(renderFriend(f));
	}
	
	/**
	 * Method to delete a friend
	 * 
	 * @param userId
	 * @return
	 */
	public static Result deleteFriend(Long userId, Long friendId) {
		String result = utils.UserUtils.unfriend(userId, friendId);
		return ok(result);
	}
	
}