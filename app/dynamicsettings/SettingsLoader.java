package dynamicsettings;

public interface SettingsLoader {

  void registerSetting(Setting<?> setting) throws NoSuchConverterFoundException;

  void refreshAll();

  class NoSuchConverterFoundException extends Exception {
    public NoSuchConverterFoundException(Setting<?> s) {
      super(
          String.format(
              "could not find suitable converter for type=\"%s\", for \"%s.%s\"",
              s.getType().getCanonicalName(),
              s.getModuleName(),
              s.getName()
          )
      );
    }
  }
}
