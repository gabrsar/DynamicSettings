package dynamicsettings;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class ConverterProvider {

  private ConverterProvider() {
  }

  private static final Map<Class<?>, Parser<?>> typeConverterMap = initializeConversionMap();

  private interface Parser<T> {
    T parse(AttributeValue v);
  }

  private static Map<Class<?>, Parser<?>> initializeConversionMap() {
    Map<Class<?>, Parser<?>> map = new HashMap<>();
    map.put(String.class, AttributeValue::getS);
    map.put(Boolean.class, AttributeValue::getBOOL);
    map.put(Integer.class, v -> Integer.parseInt(v.getN()));
    map.put(Float.class, v -> Float.parseFloat(v.getN()));
    map.put(Double.class, v -> Double.parseDouble(v.getN()));
    map.put(BigDecimal.class, v -> new BigDecimal(v.getN()));

    // TODO: Implement converter to Set and Arrays. Maybe simply handle them as String json and make a hard parse?
    return map;
  }

  static boolean hasConverter(Class<?> clazz) {
    return typeConverterMap.containsKey(clazz);
  }

  static Optional<Object> convert(AttributeValue v, Setting s) {
    return Optional.of(typeConverterMap.get(s.getType())).map(p -> p.parse(v));
  }

}

