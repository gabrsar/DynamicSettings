package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.dummy;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Clazz;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Setting;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.NotSupportedTypeException;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.SettingsValueProvider;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyProvider implements SettingsValueProvider {

    private static final Logger logger = LoggerFactory.getLogger(DummyProvider.class);
    private static final Clazz supportedType = new Clazz(String.class);
    private String currentRoundValue = "unitialized";

    public DummyProvider() {
        logger.warn("+=============================================================================================+");
        logger.warn("| YOU ARE USING DUMMY PROVIDER. IT WILL NOT GET ANY USEFUL VALUE. ITS ONLY FOR DEMONSTRATING! |");
        logger.warn("+=============================================================================================+");
    }

    @Override
    public void loadAll() {
        currentRoundValue = new Date().toString();
    }

    @Override
    public void assertSupportedType(Setting<?> s) throws NotSupportedTypeException {
        if (!s.getType().equals(supportedType)) {
            throw new NotSupportedTypeException(this, s);
        }
    }

    @Override
    public List<Clazz> getSupportedTypes() {
        return Collections.singletonList(supportedType);
    }

    @Override
    public Optional<Object> getSettingValue(Setting<?> setting) {

        String newValue = String.format(
            "%s.%s-%s",
            setting.getModule(),
            setting.getName(),
            currentRoundValue
        );

        return Optional.of(newValue);
    }


}

