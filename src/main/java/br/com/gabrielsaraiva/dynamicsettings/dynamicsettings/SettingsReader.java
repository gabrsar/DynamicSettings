package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

/** This class is used to get values of the settings it self.
 * It's really usefull because it removes the acoplation that static
 * properties will put into your code while allowing it to be easily testable due simply mocking.
 */

public class SettingsReader {

    /** Receives a setting and returns its current value.*/
    public <T> T get(Setting<T> setting){
        return setting.getValue();
    }

}
