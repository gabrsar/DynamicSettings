package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

import com.sun.istack.internal.NotNull;
import java.util.Optional;

public class Setting<T> {

    private final String name;
    private final T fallBackValue;
    private String moduleName;
    private T currentValue;

    // Maybe check if still using a birth value?

    private Setting(@NotNull String name, @NotNull T fallbackValue) {
        this.name = name;
        this.fallBackValue = fallbackValue;
        this.currentValue = fallbackValue;
    }

    public static <A> Setting<A> define(@NotNull String name, @NotNull A fallbackValue) {
        return new Setting<>(name, fallbackValue);
    }

    void setModuleName(String moduleName) {
        this.moduleName = moduleName;
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

    public String getModuleName() {
        return moduleName;
    }

    public String getName() {
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
