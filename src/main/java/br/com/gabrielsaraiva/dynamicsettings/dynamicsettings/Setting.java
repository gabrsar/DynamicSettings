package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

import java.util.Optional;

public class Setting<T> {

    private final String name;
    private final T fallBackValue;
    private String moduleName;
    private T currentValue;

    // Maybe check if still using a birth value?

    private Setting(String name, T fallbackValue) {
        this.name = name;
        this.fallBackValue = fallbackValue;
        this.currentValue = fallbackValue;
    }

    public static <A> Setting<A> define(String name, A fallbackValue) {
        return new Setting<>(name, fallbackValue);
    }

    protected void setModuleName(String moduleName) {

        if (this.moduleName != null) {
            throw new RegisterSettingException("module name already defined");
        }

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
        if (moduleName == null) {
            throw new RegisterSettingException("module name was not defined yet");
        }
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

    public String getIdentification() {
        return String
            .format(
                "moduleName=%s, name=%s, fallbackValue=%s, value=%s",
                moduleName,
                name,
                String.valueOf(fallBackValue),
                String.valueOf(currentValue)
            );
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
