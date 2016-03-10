import java.util.List;

import java.util.Map;

import com.avaje.ebean.Ebean;

import models.User;
import play.Application;
import play.GlobalSettings;
import play.libs.Yaml;

/**
 * This class is used to insert initial data into the DB
 * @author colmcarew
 *
 */
@SuppressWarnings("unchecked")
public class Global extends GlobalSettings

{
  public void onStart(Application app)
  {
    InitialData.insert(app);
  }

  static class InitialData
  {
    public static void insert(Application app)
    {
      if (Ebean.find(User.class).findRowCount() == 0)
      {
        Map<String, List<User>> all = (Map<String, List<User>>) Yaml.load("initial-data.yml");
        Ebean.save(all.get("users"));
      }
    }
  }
}