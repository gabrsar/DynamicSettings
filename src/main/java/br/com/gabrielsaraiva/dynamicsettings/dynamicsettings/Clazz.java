package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

import java.util.Arrays;
import java.util.Objects;

public class Clazz {

    private final Class<?> baseClass;

    private final Class<?>[] types;

    public Clazz(Class<?> baseClass, Class<?>... types) {
        this.baseClass = baseClass;
        this.types = types;
    }

    public Class<?> getBaseClass() {
        return baseClass;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Clazz)) {
            return false;
        }
        Clazz clazz = (Clazz) o;

        return Objects.equals(getBaseClass(), clazz.getBaseClass()) &&
            Arrays.toString(getTypes()).equals(Arrays.toString(clazz.getTypes()));
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getBaseClass());
        result = 31 * result + Arrays.hashCode(getTypes());
        return result;
    }

    @Override
    public String toString() {
        return "Clazz{" +
            "baseClass=" + baseClass +
            ", types=" + Arrays.toString(types) +
            '}';
    }
}
