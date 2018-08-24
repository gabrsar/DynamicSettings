package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

public class SettingsReader {

    public <T> T get(Setting<T> setting){
        return setting.getValue();
    }

}
