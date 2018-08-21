package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Setting;
import java.util.Optional;

public interface SettingsValueProvider {

    void loadAll();

    Optional<Object> getSettingValue(Setting<?> setting);

    public void assertSupportedType(Setting<?> s) throws NotSupportedTypeException;


}