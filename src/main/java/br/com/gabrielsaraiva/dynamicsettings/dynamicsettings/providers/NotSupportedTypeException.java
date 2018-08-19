package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Setting;

public class NotSupportedTypeException extends Exception {

    public NotSupportedTypeException(SettingsValueProvider provider, Setting<?> s) {
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
