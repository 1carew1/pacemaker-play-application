package controllers;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import models.Activity;
import models.Location;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.dashboard_main;
import views.html.dashboard_manageroutes;
import views.html.dashboard_settings;
import views.html.dashboard_uploadactivity;
/**
 * Controller used to display and edit the user's activities and location
 * @author colmcarew
 *
 */
public class Dashboard extends Controller {
	private static final Form<Activity> activityForm = Form.form(Activity.class);
	
	/**
	 * Return the main dashboard of the logged in user
	 * @return
	 */
	public static Result index() {
		Result result = null;
		String email = session().get("email");
		User user = User.findByEmail(email);
		if (user == null) {
			Logger.info(new Date() + " User is null cannot make it to dashboard");
			result = redirect(routes.Accounts.login());
		} else {
			result = ok(dashboard_main.render(user.activities));
		}
		return result;
	}
	
	/**
	 * Upload activity form used to create an activity
	 * @return
	 */
	public static Result uploadActivityForm() {
		return ok(dashboard_uploadactivity.render(""));
	}
	
	/**
	 * Render the user settings page
	 * @return
	 */
	public static Result userSettings() {
		return ok(dashboard_settings.render(""));
	}
	
	/**
	 * Form the pull the check box values for the privacy settings
	 * @return
	 */
	public static Result submitSettings() {
		User u = User.findByEmail(session().get("email"));
		DynamicForm requestData = Form.form().bindFromRequest();
		String isPublicViewable = requestData.get("public");
		String isFriendViewable = requestData.get("friends");
		Boolean publicBox = (isPublicViewable != null) ? isPublicViewable.equals("on") : false;
		Boolean friendBox = (isFriendViewable != null) ? isFriendViewable.equals("on") : false;
		friendBox = (publicBox) ? publicBox : friendBox;
		// If your actiities are viewable to everyone they are viewable to your
		// friends
		u.isPublicViewable = publicBox;
		u.isFriendViewable = friendBox;
		Logger.info(new Date() + " Saving " + u.firstname + " " + u.lastname + "'s settings");
		u.save();
		return ok(dashboard_settings.render(""));
	}

	/**
	 * Pull necessary data from activity form and create the activity
	 * @return
	 */
	public static Result submitActivity() {

		DynamicForm requestData = Form.form().bindFromRequest();
		Result result = null;
		String errorMessage = "";
		String date = requestData.get("startDate");
		if (date.matches("\\d{4}\\-\\d{2}\\-\\d{2}")) {
			String time = requestData.get("startTime");
			if (time.matches("\\d{2}\\:\\d{2}")) {
				String duration = requestData.get("duration");
				if (duration.matches("\\d{2}\\:\\d{2}")) {
					String distanceString = requestData.get("distance");
					if (distanceString.matches("\\d+")) {
						Double distance = Double.parseDouble(distanceString);
						String kind = requestData.get("kind");
						String location = requestData.get("location");
						String dateTime = date + " " + time;
						DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
						DateTime activietyDateTime = formatter.parseDateTime(dateTime);
//Problem with startTime
//						PeriodFormatter hoursMinutes = new PeriodFormatterBuilder().appendHours().appendSeparator(":")
//								.appendMinutes().toFormatter();
//						Period p = hoursMinutes.parsePeriod(duration);
//						Duration timeDuration = p.toStandardDuration();
//						String actualDuration = timeDuration.getStandardHours() + " Hour/s and "
//								+ (timeDuration.getStandardMinutes() - timeDuration.getStandardHours() * 60)
//								+ " Minutes";

						Activity activity = new Activity(kind, location, distance, activietyDateTime, duration);
						User user = User.findByEmail(session().get("email"));
						Logger.info(new Date() + " " + user.firstname + " " + user.lastname + " added an activity");
						user.activities.add(activity);
						user.save();
						result = redirect(routes.Dashboard.index());
					} else {
						Logger.info(new Date() + " Error creating activity, bad distance");
						errorMessage += "Please Input a correct distance, this: " + distanceString
								+ ", is not a correct distance";
						result = badRequest(dashboard_uploadactivity.render(errorMessage));
					}
				} else {
					Logger.info(new Date() + " Error creating activity, duration is wrong");
					errorMessage += "Please Input a correct duration, this: " + duration
							+ ", is not a correct duration";
					result = badRequest(dashboard_uploadactivity.render(errorMessage));
				}
			} else {
				Logger.info(new Date() + " Error creating activity, time is wrong");
				errorMessage += "Please Input a correct time, this: " + time + ", is not a valid time";
				result = badRequest(dashboard_uploadactivity.render(errorMessage));
			}
		} else {
			Logger.info(new Date() + " Error creating activity, date is wrong");
			errorMessage += "Please Input a correct date this: " + date.toString() + ", is not a valid date";
			result = badRequest(dashboard_uploadactivity.render(errorMessage));
		}

		if (activityForm.hasErrors()) {
			errorMessage = "Uknown errors in activity form";
			Logger.info(new Date() + " Problem with activity form: " + errorMessage);
			result = badRequest(dashboard_uploadactivity.render(errorMessage));
		}
		return result;
	}
	
	/**
	 * Pull location information from the location form
	 * @param activityId
	 * @return
	 */
	public static Result submitLocation(Long activityId) {
		Result result = null;
		String refererUrl = request().getHeader("referer");
		DynamicForm requestData = Form.form().bindFromRequest();
		String latitudeString = requestData.get("latitude");
		String longitudeString = requestData.get("longitude");
		if (longitudeString.matches("\\-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)")
				&& latitudeString.matches("\\-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+)")) {
			float latitude = Float.parseFloat(latitudeString);
			float longitude = Float.parseFloat(longitudeString);
			Location location = new Location(latitude, longitude);
			Activity activity = Activity.findById(activityId);
			activity.routes.add(location);
			activity.save();
			Logger.info(new Date() + " Creating new location " + location.toString());
			result = redirect(routes.Dashboard.index());
		} else {
			Logger.info(new Date() + " Error in creating location " + latitudeString + ", " + longitudeString);
			result = redirect(refererUrl);
		}
		return result;
	}
	
	/**
	 * Delete an activity given the user id and the activity id
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public static Result deleteActivity(Long userId, Long activityId) {
		User user = User.findById(userId);
		Activity activity = Activity.findById(activityId);
		if(user != null && activity != null) {
			user.activities.remove(activity);
			activity.delete();
			Logger.info(new Date() + " Activity deleted for " + user.firstname + " " + user.lastname);
			user.save();	
		} else {
			Logger.info(new Date() + " User or Activity was null so could not delete");
		}

		return redirect(routes.Dashboard.index());
	}
	
	/**
	 * Render the page to manage the routes of the activities
	 * @return
	 */
	public static Result manageRoutes() {
		return ok(dashboard_manageroutes.render());
	}
	
	/**
	 * Delete a route give the activity id and the route id
	 * @param activityId
	 * @param routeId
	 * @return
	 */
	public static Result deleteRoute(Long activityId, Long routeId) {
		Activity activity = Activity.findById(activityId);
		Location route = Location.findById(routeId);
		if (activityForm.hasErrors()) {
			Logger.info(new Date() + " Activity form has errors");
			return badRequest();
		}
		if (route != null && activity != null) {
			activity.routes.remove(route);
			route.delete();
			activity.save();
			Logger.info(new Date() + " Route removed");
		} else {
			Logger.info(new Date() + " Route or activity was null");
		}
		return ok(dashboard_manageroutes.render());
	}
}
