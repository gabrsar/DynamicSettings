package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.SettingsValueProvider;

public interface SettingsLoader {


    void start();

    void registerSetting(Setting<?> setting) throws InvalidTypeForProvider;

    void refreshAll();

    class InvalidTypeForProvider extends Exception {

        public InvalidTypeForProvider(SettingsValueProvider provider, Setting<?> s) {
            super(
                String.format(
                    "Provider=\"%s\" does not have support for type=\"%s\", used by \"%s.%s\"",
                    provider.getClass().getCanonicalName(),
                    s.getType().getCanonicalName(),
                    s.getModuleName(),
                    s.getName()
                )
            );
        }
    }
}
