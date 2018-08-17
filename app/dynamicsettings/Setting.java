package dynamicsettings;

import javax.annotation.Nonnull;
import java.util.Optional;

public class Setting<T> {

  private final String moduleName;
  private final String name;
  private final T fallBackValue;
  private T currentValue;

  // Maybe check if still using a born value?

  private Setting(@Nonnull String pk, @Nonnull String name, @Nonnull T fallbackValue) {
    this.moduleName = pk;
    this.name = name;
    this.fallBackValue = fallbackValue;
    this.currentValue = fallbackValue;
  }

  public static <A> Setting<A> setting(@Nonnull String moduleName, @Nonnull String name, @Nonnull A fallbackValue) {
    return new Setting<>(moduleName, name, fallbackValue);
  }

  public T getValue() {
    return currentValue;
  }

  public Class<?> getType() {
    return fallBackValue.getClass();
  }

  T getFallBackValue() {
    return fallBackValue;
  }

  String getModuleName() {
    return moduleName;
  }

  String getName() {
    return name;
  }


  boolean updateValue(Optional<Object> rawNewValueOpt) {

    if (!rawNewValueOpt.isPresent()) {
      return false;
    }

    Object rawValue = rawNewValueOpt.get();

    @SuppressWarnings("unchecked")
    T value = (T) rawValue;

    currentValue = value;
    return true;

  }

  @Override
  public String toString() {
    return "Setting{" +
        "moduleName='" + moduleName + '\'' +
        ", name='" + name + '\'' +
        ", fallBackValue=" + fallBackValue +
        ", value=" + currentValue +
        '}';
  }
}
