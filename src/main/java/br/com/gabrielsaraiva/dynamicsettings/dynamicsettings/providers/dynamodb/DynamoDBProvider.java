package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.dynamodb;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Clazz;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Setting;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.NotSupportedTypeException;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.SettingsValueProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.joda.time.DateTime;

public class DynamoDBProvider implements SettingsValueProvider {

    private static final Map<Clazz, Parser> typeConverterMap = getParserMap();
    private Map<String, Map<String, AttributeValue>> modulesDataByModulesName = new HashMap<>();

    private final String tableName;

    private final AmazonDynamoDB dynamoClient;

    public DynamoDBProvider(String tableName) {
        this(AmazonDynamoDBClientBuilder.defaultClient(), tableName);
    }

    public DynamoDBProvider(AmazonDynamoDB dynamoDB, String tableName) {
        this.tableName = tableName;
        this.dynamoClient = dynamoDB;
    }

    public List<Clazz> getSupportedTypes() {
        return typeConverterMap.entrySet().stream().map(Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public void loadAll() {

        ScanResult all = dynamoClient.scan(new ScanRequest(tableName));
        List<Map<String, AttributeValue>> items = all.getItems();

        Map<String, List<Map<String, AttributeValue>>> modules = items.stream()
            .collect(Collectors.groupingBy(a -> a.get("module").getS()));

        modulesDataByModulesName = modules.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

    }

    public void assertSupportedType(Setting<?> s) throws NotSupportedTypeException {
        System.out.println("all = " + typeConverterMap);
        if (!typeConverterMap.containsKey(s.getType())) {
            throw new NotSupportedTypeException(this, s);
        }

    }

    @Override
    public Optional<Object> getSettingValue(Setting<?> setting) {

        return Optional
            .ofNullable(modulesDataByModulesName.get(setting.getModuleName()))
            .map(module -> module.get(setting.getName()))
            .flatMap(va -> convert(va, setting));
    }

    private Optional<Object> convert(AttributeValue v, Setting s) {
        Parser parser = typeConverterMap.get(s.getType());
        return Optional.ofNullable(v).map(parser::parse);
    }

    private static Map<Clazz, Parser> getParserMap() {

        Map<Clazz, Parser> parserMap = new HashMap<>();

        List<ClazzParser> numbers = Arrays.asList(
            new ClazzParser(Integer.class, v -> Integer.parseInt(v.getN())),
            new ClazzParser(Float.class, v -> Float.parseFloat(v.getN())),
            new ClazzParser(Double.class, v -> Double.parseDouble(v.getN())),
            new ClazzParser(BigDecimal.class, v -> new BigDecimal(v.getN()))
        );

        List<ClazzParser> stringBased = Arrays.asList(
            new ClazzParser(String.class, AttributeValue::getS),
            new ClazzParser(DateTime.class, v -> new DateTime(v.getS())),
            new ClazzParser(Date.class, v -> new DateTime(v.getS()).toDate())
        );

        List<ClazzParser> others = Collections.singletonList(
            // TODO Implement byte buffer
            new ClazzParser(Boolean.class, AttributeValue::getBOOL)
        );

        List<ClazzParser> all = new ArrayList<>();
        all.addAll(numbers);
        all.addAll(stringBased);
        all.addAll(others);

        all.forEach(cp -> {
            parserMap.put(cp.getClazz(), cp.getParser());
            parserMap.put(makeMapClazz(cp.getClazz()), DynamoDBProvider.makeMapParser(cp));
            parserMap.put(makeListClazz(cp.getClazz()), DynamoDBProvider.makeListParser(cp));
        });

        numbers.forEach(cp -> parserMap.put(makeSetClazz(cp.getClazz()), makeNumberSetParser(cp)));

        parserMap.put(makeSetClazz(new Clazz(String.class)), makeStringSetParser());

        System.out.println("all = " + all);

        return parserMap;

    }

    private static Clazz makeMapClazz(Clazz clazz) {
        return new Clazz(Map.class, String.class, clazz.getBaseClass());
    }

    private static Clazz makeSetClazz(Clazz clazz) {
        return new Clazz(Set.class, clazz.getBaseClass());
    }

    private static Clazz makeListClazz(Clazz clazz) {
        return new Clazz(List.class, clazz.getBaseClass());
    }

    private static Parser makeStringSetParser() {
        return (p) -> new HashSet<>(p.getSS());
    }

    private static Parser makeNumberSetParser(ClazzParser cp) {
        return (p) -> (Set) p.getNS().stream().map(v -> cp.getParser().parse(new AttributeValue(v)));
    }


    private static Parser makeListParser(ClazzParser cp) {
        return (p) -> (ArrayList) p.getL().stream()
            .map(v -> cp.getParser().parse(v))
            .collect(Collectors.toList());
    }

    private static Parser makeMapParser(ClazzParser cp) {
        return (p) -> (HashMap) p.getM()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(Entry::getKey, entry -> cp.getParser().parse(entry.getValue()))
            );
    }

}

