package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.dynamodb;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Setting;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.NotSupportedTypeException;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.SettingsValueProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class DynamoDBProvider implements SettingsValueProvider {

    private static final Map<Class<?>, Parser<?>> typeConverterMap = initializeConversionMap();
    private Map<String, Map<String, AttributeValue>> modulesByName = new HashMap<>();

    private final String tableName;

    private final AmazonDynamoDB dynamoClient;

    public DynamoDBProvider(String tableName) {
        this(AmazonDynamoDBClientBuilder.defaultClient(), tableName);
    }

    public DynamoDBProvider(AmazonDynamoDB dynamoDB, String tableName) {
        this.tableName = tableName;
        this.dynamoClient = dynamoDB;
    }

    @Override
    public void loadAll() {

        ScanResult all = dynamoClient.scan(new ScanRequest(tableName));
        List<Map<String, AttributeValue>> items = all.getItems();

        Map<String, List<Map<String, AttributeValue>>> modules = items.stream()
            .collect(Collectors.groupingBy(a -> a.get("module").getS()));

        modulesByName = modules.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

    }

    public void assertSupportedType(Setting<?> s) throws NotSupportedTypeException {
        if (!typeConverterMap.containsKey(s.getType())) {
            throw new NotSupportedTypeException(this, s);
        }

    }

    @Override
    public Optional<Object> getSettingValue(Setting<?> setting) {
        return Optional
            .ofNullable(modulesByName.get(setting.getModuleName()))
            .map(module -> module.get(setting.getName()))
            .flatMap(va -> convert(va, setting));
    }

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

    List<Class> getSupportedTypes() {
        return typeConverterMap
            .entrySet()
            .stream()
            .map(Entry::getKey)
            .collect(Collectors.toList());
    }

    private Optional<Object> convert(AttributeValue v, Setting s) {
        return Optional.of(typeConverterMap.get(s.getType())).map(p -> p.parse(v));
    }

}

