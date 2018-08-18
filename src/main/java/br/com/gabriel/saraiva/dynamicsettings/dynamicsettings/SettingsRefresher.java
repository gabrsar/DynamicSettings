package br.com.gabriel.saraiva.dynamicsettings.dynamicsettings;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;


// TODO: Make it a singleton
class SettingsRefresher {

    private static final Logger logger = LoggerFactory.getLogger(SettingsRefresher.class);

    private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private final SettingsService settingsService;

    private static boolean settingsRegistered = false;
    private static boolean initialized = false;

    public SettingsRefresher(SettingsService settingsService, Class... sourceClasses) {
        this.settingsService = settingsService;
        Arrays.stream(sourceClasses).forEach(this::registerSettings);
    }

    public void start() {

        if (!settingsRegistered) {
            throw new RuntimeException("no settings registered before start.");
        }

        if (initialized) {
            throw new RuntimeException("already stated.");
        }

        logger.debug("starting");
        initialized = true;
        scheduledExecutor.scheduleWithFixedDelay(this::refreshAll, 0, 5, SECONDS);
    }

    private void refreshAll() {
        logger.info("refreshing {}", DateTime.now());
        settingsService.refreshAll();
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


    private void loadModule(Class module) {

        logger.debug("Loading settings from '{}'", module.getCanonicalName());

        for (Field field : module.getFields()) {
            try {
                Setting<?> setting = (Setting<?>) field.get(Setting.class);

                settingsService.registerSetting(setting);
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

    private static class RegisterSettingException extends RuntimeException {

        RegisterSettingException(Throwable e) {
            super(e);
        }
    }

}
