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
        if (!typeConverterMap.containsKey(s.getType())) {
            throw new NotSupportedTypeException(this, s);
        }

    }

    @Override
    public Optional<Object> getSettingValue(Setting<?> setting) {

        return Optional
            .ofNullable(modulesDataByModulesName.get(setting.getModule()))
            .map(module -> module.get(setting.getName()))
            .flatMap(va -> convert(va, setting));
    }

    private Optional<Object> convert(AttributeValue v, Setting s) {
        Parser parser = typeConverterMap.get(s.getType());
        return Optional.ofNullable(v).map(parser::parse);
    }

    private static Map<Clazz, Parser> getParserMap() {

        /* Here is generated a list of MetaParser for all supported types,
         * from simple ones (String, Integer, ...) to complex ones like
         * (List of Strings, Map of List of Strings, ...)
         */

        List<MetaParser> numbers = Arrays.asList(
            new MetaParser(Integer.class, v -> Integer.parseInt(v.getN())),
            new MetaParser(Float.class, v -> Float.parseFloat(v.getN())),
            new MetaParser(Double.class, v -> Double.parseDouble(v.getN())),
            new MetaParser(BigDecimal.class, v -> new BigDecimal(v.getN()))
        );

        List<MetaParser> stringBased = Arrays.asList(
            new MetaParser(String.class, AttributeValue::getS),
            new MetaParser(DateTime.class, v -> new DateTime(v.getS())),
            new MetaParser(Date.class, v -> new DateTime(v.getS()).toDate())
        );

        List<MetaParser> others = Collections.singletonList(
            new MetaParser(Boolean.class, AttributeValue::getBOOL)
        );

        List<MetaParser> allMetaParsers = new ArrayList<>();
        allMetaParsers.addAll(numbers);
        allMetaParsers.addAll(stringBased);
        allMetaParsers.addAll(others);

        /* Now with MetaParsers for all supported types
         * it's created the map of Parsers that will effectively do the parsing from Dynamo.
         */

        Map<Clazz, Parser> parserMap = new HashMap<>();

        allMetaParsers.forEach(meta -> {
            parserMap.put(meta.getClazz(), meta.getParser());
            parserMap.put(makeMapClazz(meta.getClazz()), makeMapParser(meta.getParser()));
            parserMap.put(makeListClazz(meta.getClazz()), makeListParser(meta.getParser()));
        });

        // Only supports Sets of String, Numbers and Binary (not implemented yet)
        // But with that, all string based types are supported.
        stringBased.forEach(meta ->
            parserMap.put(makeSetClazz(meta.getClazz()), makeStringSetParser(meta.getParser()))
        );

        numbers.forEach(meta ->
            parserMap.put(makeSetClazz(meta.getClazz()), makeNumberSetParser(meta.getParser()))
        );

        // Basic classes, List, Maps, and Sets parsers
        return parserMap;

    }

    // Here are support methods to make meta assembly easier

    private static Clazz makeMapClazz(Clazz clazz) {
        return new Clazz(Map.class, String.class, clazz.getBaseClass());
    }

    private static Clazz makeSetClazz(Clazz clazz) {
        return new Clazz(Set.class, clazz.getBaseClass());
    }

    private static Clazz makeListClazz(Clazz clazz) {
        return new Clazz(List.class, clazz.getBaseClass());
    }

    private static Parser makeStringSetParser(Parser parser) {
        return (av) -> (Set) av.getSS().stream().map(AttributeValue::new).map(parser::parse);
    }

    private static Parser makeNumberSetParser(Parser parser) {
        return (av) -> (Set) (av.getNS().stream().map(AttributeValue::new).map(parser::parse));
    }

    private static Parser makeListParser(Parser parser) {
        return (av) -> (ArrayList) av.getL().stream().map(parser::parse).collect(Collectors.toList());
    }

    private static Parser makeMapParser(Parser parser) {
        return (av) -> (HashMap) av.getM()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(Entry::getKey, entry -> parser.parse(entry.getValue()))
            );
    }

}

