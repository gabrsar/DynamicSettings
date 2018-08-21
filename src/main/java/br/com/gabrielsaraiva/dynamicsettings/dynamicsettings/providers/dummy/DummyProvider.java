package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.dummy;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Setting;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.NotSupportedTypeException;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.SettingsValueProvider;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyProvider implements SettingsValueProvider {

    private static final Logger logger = LoggerFactory.getLogger(DummyProvider.class);
    private static final Class<String> supportedType = String.class;
    private String currentRoundValue = "unitialized";

    public DummyProvider() {
        logger.warn("+=======================================================================================+");
        logger.warn("| YOU ARE USING DUMMY PROVIDER. IT WILL NOT GET ANY USEFUL VALUE. ITS ONLY FOR TESTING! |");
        logger.warn("+=======================================================================================+");
    }

    @Override
    public void loadAll() {
        currentRoundValue = new Date().toString();
    }

    @Override
    public void assertSupportedType(Setting<?> s) throws NotSupportedTypeException {
        if (s.getType() != supportedType) {
            throw new NotSupportedTypeException(this, s);
        }
    }

    @Override
    public Optional<Object> getSettingValue(Setting<?> setting) {

        String newValue = String.format(
            "%s.%s-%s",
            setting.getModuleName(),
            setting.getName(),
            currentRoundValue
        );

        return Optional.of(newValue);
    }


}

