package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

import java.util.Optional;

public class Setting<T> {

    private boolean ready = false;

    private String name;
    private String module;
    private T fallBackValue;
    private T currentValue;
    private Clazz type;

    private Setting(T fallbackValue, Clazz type) {
        this.fallBackValue = fallbackValue;
        this.currentValue = fallbackValue;
        this.type = type;
    }

    public static <A> Setting<A> define(A fallbackValue) {
        return new Setting<>(fallbackValue, new Clazz(fallbackValue.getClass()));
    }

    public static <A> Setting<A> define(A fallbackValue, Class base, Class... inner) {
        return new Setting<>(fallbackValue, new Clazz(base, inner));
    }

    protected void register(String module, String name) {

        if (ready) {
            throw new RegisterSettingException("module name already defined");
        }

        this.module = module;
        this.name = name;
        this.ready = true;
    }

    T getValue() {
        return currentValue;
    }

    public Clazz getType() {
        return type;
    }

    T getFallBackValue() {
        return fallBackValue;
    }

    public String getModule() {
        if (module == null) {
            throw new RegisterSettingException("module name was not defined yet");
        }
        return module;
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

    // TODO: This approach is not nice. Remove this method.
    public String getIdentification() {
        return String
            .format(
                "module=%s, name=%s, fallbackValue=%s, value=%s",
                module,
                name,
                String.valueOf(fallBackValue),
                String.valueOf(currentValue)
            );
    }

    @Override
    public String toString() {
        return "Setting{" +
            "module='" + module + '\'' +
            ", name='" + name + '\'' +
            ", fallBackValue=" + fallBackValue +
            ", value=" + currentValue +
            '}';
    }
}
