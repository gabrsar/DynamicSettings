package dynamicsettings;

import org.joda.time.DateTime;
import play.Logger;

import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

@Singleton
public class SettingsWorker {

  private static final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
  private static SettingsService settingsService;

  private static boolean initialized = false;

  public SettingsWorker(SettingsService settingsService, Class... sourceClasses) {
    this.settingsService = settingsService;
    Arrays.stream(sourceClasses).forEach(this::registerSettings);
  }

  public void start() {

    if (!initialized) {
      throw new RuntimeException("no settings registered before start.");
    }

    Logger.debug("starting");
    scheduledExecutor.scheduleWithFixedDelay(this::refreshAll, 0, 5, SECONDS);
  }

  private void refreshAll() {
    Logger.info("refreshing {}", DateTime.now());
    settingsService.refreshAll();
  }

  private void registerSettings(Class<?> clazz) {
    try {
      Class<?>[] settingsGroup = clazz.getDeclaredClasses();

      Arrays.stream(settingsGroup).forEach(this::loadModule);

      initialized = true;
    } catch (Exception e) {
      initialized = false;
      throw new RegisterSettingException(e);
    }
  }


  private void loadModule(Class module) {

    Logger.debug("Loading settings from '{}'", module.getCanonicalName());

    for (Field field : module.getFields()) {
      try {
        Setting<?> setting = (Setting<?>) field.get(Setting.class);


        settingsService.registerSetting(setting);
        Logger.debug(
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
