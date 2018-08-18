package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.SettingsValueProvider;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsService implements SettingsLoader {

    private static final Logger logger = LoggerFactory.getLogger(SettingsService.class);

    private static final Set<Setting<?>> settings = new HashSet<>();
    private final SettingsValueProvider provider;
    private final SettingsRefresher refresher;

    public SettingsService(SettingsValueProvider provider, Class... sourceClasses) {
        this.provider = provider;
        this.refresher = new SettingsRefresher(this, sourceClasses);
    }

    @Override
    public void start() {
        refresher.start();
    }

    public void registerSetting(Setting<?> setting) throws InvalidTypeForProvider {
        if (!provider.accept(setting.getType())) {
            throw new InvalidTypeForProvider(provider, setting);
        }
        settings.add(setting);
    }

    public void refreshAll() {
        provider.loadAll();
        settings.forEach(s -> updateSettingValue(s, provider.getSettingValue(s)));
    }

    private void updateSettingValue(Setting<?> setting, Optional<Object> rawValueOpt) {

        boolean updated = setting.updateValue(rawValueOpt);

        logger.debug(
            "{}.{}={} ({}), updated={}, found={}",
            setting.getModuleName(),
            setting.getName(),
            setting.getValue(),
            setting.getFallBackValue(),
            updated,
            rawValueOpt.isPresent()
        );
    }
}
