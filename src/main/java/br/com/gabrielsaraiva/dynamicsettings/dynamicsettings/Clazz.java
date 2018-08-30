package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

import java.util.Arrays;
import java.util.Objects;

public class Clazz {

    /**
     * Due Java type erasure, it's necessary to do a few tricky things to keep all information required to parse data
     * back.
     *
     * As type of something isn't known at runtime, its necessary to keep it as a meta information.
     * Its done in this way:
     *
     * A Clazz represent full type information of some item:
     *
     * For example:
     *  - To represent a List<String> it's used an object Clazz(List.class,String.class)
     *  - To represent a Map<String,Integer> the object is Clazz(Map.class,String.class,Integer.class)
     *  - To represent a Map<String,List<Integer>> the object is
     *     Clazz(Map.class,String.class,Clazz(List.class, String.class))
     *
     */

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
