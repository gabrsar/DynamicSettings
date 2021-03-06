package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

import static java.util.concurrent.TimeUnit.SECONDS;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.NotSupportedTypeException;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.SettingsValueProvider;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicSettings {

    private static final Logger logger = LoggerFactory.getLogger(DynamicSettings.class);

    private boolean settingsRegistered = false;
    private boolean initialized = false;

    private final Set<Setting<?>> settings = new HashSet<>();

    private final ScheduledExecutorService refresher;
    private final int refreshIntervalInSeconds;
    private final SettingsValueProvider provider;

    public DynamicSettings(SettingsValueProvider provider, int refreshIntervalInSeconds, Class sourceClass,
        Class... sourceClasses) {
        this.provider = provider;
        this.refreshIntervalInSeconds = refreshIntervalInSeconds;
        this.refresher = Executors.newSingleThreadScheduledExecutor();

        List<Class> sources = new ArrayList<>(1 + sourceClasses.length);
        sources.add(sourceClass);
        sources.addAll(Arrays.asList(sourceClasses));

        sources.forEach(this::registerSettings);

    }

    private void refreshAll() {
        boolean success;
        try {
            provider.loadAll();
            settings.forEach(s -> updateSettingValue(s, provider.getSettingValue(s)));
            success = true;
        } catch (Exception e) {
            logger.error("could not refresh all settings.", e);
            success = false;
        }

        logger.info("Settings refresh done, success={}, next refresh in={} seconds", success, refreshIntervalInSeconds);
    }

    private void registerSettings(Class<?> clazz) {
        try {
            Class<?>[] settingsGroup = clazz.getDeclaredClasses();

            Arrays.stream(settingsGroup).forEach(this::loadModule);

            settingsRegistered = true;
        } catch (Exception e) {
            settingsRegistered = false;
            throw new RegisterSettingException(e);
        }
    }

    private void registerForRefresh(Setting<?> setting) throws NotSupportedTypeException {
        provider.assertSupportedType(setting);

        if (!settings.add(setting)) {
            throw new RegisterSettingException(
                String.format(
                    "%s.%s already registred",
                    setting.getModule(),
                    setting.getName()
                )
            );
        }
    }

    public void start() {

        if (!settingsRegistered) {
            throw new RegisterSettingException("could not start due error on settings' registry.");
        }

        if (initialized) {
            throw new RegisterSettingException("already started.");
        }

        refreshAll();

        logger.debug("starting with {} settings registered", settings.size());
        initialized = true;
        refresher.scheduleWithFixedDelay(this::refreshAll, refreshIntervalInSeconds, refreshIntervalInSeconds, SECONDS);
    }


    public void stop() {
        refresher.shutdown();
        initialized = false;
    }

    private void updateSettingValue(Setting<?> setting, Optional<Object> rawValueOpt) {

        boolean updated = setting.updateValue(rawValueOpt);

        logger.debug(
            "{}.{}={} ({}), updated={}, found={}",
            setting.getModule(),
            setting.getName(),
            setting.getValue(),
            setting.getFallBackValue(),
            updated,
            rawValueOpt.isPresent()
        );
    }


    private void loadModule(Class module) {

        logger.debug("Loading settings from '{}'", module.getCanonicalName());

        String moduleName = module.getSimpleName();

        for (Field field : module.getFields()) {
            try {
                Setting<?> setting = (Setting<?>) field.get(Setting.class);

                setting.register(moduleName, field.getName());

                registerForRefresh(setting);
                logger.debug(
                    "Setting '{}.{}' ({}) registered with default value='{}'",
                    setting.getModule(),
                    setting.getName(),
                    setting.getType(),
                    setting.getFallBackValue()
                );
            } catch (Exception e) {
                throw new RegisterSettingException(e);
            }
        }
    }
}
