package br.com.gabrielsaraiva.dynamicsettings.example;

import static br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Setting.setting;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Setting;

public class Settings {

    // Create your Settings classes, or name them as you want.

    // They will be represented in Dynamo as new "documents", this one will be a record with module="Home"
    public static class Home {

        // This will help you avoid typos and avoid repeating yourself every new setting.
        private static final String home = "Home";

        // Here is your setting, with a type, module name, and its own name, ever, with a fallback value
        public static final Setting<String> address = setting(home, "address", "Some safe place to live, 123, Country");

        // Here is just another one
        public static final Setting<Integer> rooms = setting(home, "rooms", 2);

    }

    public static class School {

        private static final String school = "School";
        public static final Setting<String> address = setting(school, "name", "Some good school to study University");
        public static final Setting<Integer> year = setting(school, "year", 2010);

    }

}
