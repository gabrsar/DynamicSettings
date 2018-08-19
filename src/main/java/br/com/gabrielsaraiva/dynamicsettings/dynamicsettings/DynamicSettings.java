package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

import static java.util.concurrent.TimeUnit.SECONDS;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.NotSupportedTypeException;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.SettingsValueProvider;
import java.lang.reflect.Field;
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

    private static class Settings {

        private static class SelfSettings {

            private static final String self = DynamicSettings.class.getSimpleName();
            public static final Setting<Integer> refreshInterval = Setting.setting(self, "refreshInterval", 60);
            public static final Setting<Boolean> logs = Setting.setting(self, "logs", true);

        }

    }

    private static final Logger logger = LoggerFactory.getLogger(DynamicSettings.class);
    private static final ScheduledExecutorService refresher = Executors.newSingleThreadScheduledExecutor();

    private static boolean settingsRegistered = false;
    private static boolean initialized = false;

    private static final Set<Setting<?>> settings = new HashSet<>();

    private final SettingsValueProvider provider;

    public DynamicSettings(SettingsValueProvider provider, Class sourceClass, Class... sourceClasses) {
        this.provider = provider;

        List<Class> sources = Arrays.asList(sourceClasses);
        sources.add(sourceClass);

    }

    private void rigister(List<Class> sources) {

        sources.forEach(this::registerSettings);

        String selfSettingsModuleName = DynamicSettings.class.getSimpleName();

        boolean selfSettingsAlreadyDefinedByUser = settings.stream()
            .anyMatch(s -> selfSettingsModuleName.equals(s.getModuleName()));

        if (!selfSettingsAlreadyDefinedByUser) {
            registerSettings(Settings.class);
        }

    }

    /**
     * You can use this method in a controller or something else to externally force this to happen
     */
    private void refreshAll() {
        provider.loadAll();
        settings.forEach(s -> updateSettingValue(s, provider.getSettingValue(s)));
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

    private void registerSetting(Setting<?> setting) throws NotSupportedTypeException {
        provider.assertSupportedType(setting);
        settings.add(setting);
    }

    public void start() {

        if (!settingsRegistered) {
            throw new RuntimeException("could not start due error on settings' registry.");
        }

        if (initialized) {
            throw new RuntimeException("already stated.");
        }

        refreshAll();

        logger.debug("starting with {} settings registred", settings.size());
        initialized = true;
        refresher.scheduleWithFixedDelay(this::refreshAll, 5, 5, SECONDS);
    }


    public void stop() {
        refresher.shutdown();
        initialized = false;
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


    private void loadModule(Class module) {

        logger.debug("Loading settings from '{}'", module.getCanonicalName());

        for (Field field : module.getFields()) {
            try {
                Setting<?> setting = (Setting<?>) field.get(Setting.class);

                registerSetting(setting);
                logger.debug(
                    "Setting '{}.{}' ({}) registered with default value='{}'",
                    setting.getModuleName(),
                    setting.getName(),
                    setting.getType().getSimpleName(),
                    setting.getFallBackValue()
                );
            } catch (Exception e) {
                throw new RegisterSettingException(e);
            }

        }
    }
}
