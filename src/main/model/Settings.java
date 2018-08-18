package model;

import dynamicsettings.Setting;

public class Settings {

  public static class Home {
    private static final String home = "Home";

    public static final Setting<String> address = Setting.setting(home, "address", "José Cesarini, 9876");

  }

  public static class School {
    private static final String school = "School";

    // This in Dynamo is called only name. But it should keep working normally with default value.
    public static final Setting<String> address = Setting.setting(school, "name", "UNESP");
    public static final Setting<Integer> year = Setting.setting(school, "year", 2010);

  }

}
