package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.dynamodb;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Clazz;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Setting;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.NotSupportedTypeException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class DynamoDBProviderTest {


    private final static long NUMBER_OF_SUPPORTED_TYPES = 31;

    private final AmazonDynamoDB dynamoDB = mock(AmazonDynamoDB.class);
    private final DynamoDBProvider provider = new DynamoDBProvider(dynamoDB, "TestTable");


    @Test
    void currentSupportedTypesStillWorking() {

        // This number cannot changes without add/remove new types.
        assertEquals(NUMBER_OF_SUPPORTED_TYPES, provider.getSupportedTypes().size());

        // Group by counting
        Map<Clazz, Long> collect = provider.getSupportedTypes().stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Optional<Entry<Clazz, Long>> duplicatedType = collect.entrySet().stream()
            .filter(e -> e.getValue() > 1)
            .findAny();

        // No duplicated types where generated
        assertFalse(duplicatedType.isPresent());

    }


    @Test
    void assertThatAcceptsSupportedTypes() {

        List<Setting<?>> validSettings = Arrays.asList(

            Setting.define("this is a string"),
            Setting.define(new DateTime()),
            Setting.define(new Date()),

            Setting.define(123),
            Setting.define(123.4f),
            Setting.define(123.4),
            Setting.define(new BigDecimal("123.45678")),

            Setting.define(true),

            Setting.define(null, List.class, String.class),
            Setting.define(null, List.class, DateTime.class),
            Setting.define(null, List.class, Date.class),
            Setting.define(null, List.class, Integer.class),
            Setting.define(null, List.class, Double.class),
            Setting.define(null, List.class, Float.class),
            Setting.define(null, List.class, BigDecimal.class),
            Setting.define(null, List.class, Boolean.class),

            Setting.define(null, Map.class, String.class, String.class),
            Setting.define(null, Map.class, String.class, DateTime.class),
            Setting.define(null, Map.class, String.class, Date.class),
            Setting.define(null, Map.class, String.class, Integer.class),
            Setting.define(null, Map.class, String.class, Float.class),
            Setting.define(null, Map.class, String.class, Double.class),
            Setting.define(null, Map.class, String.class, BigDecimal.class),
            Setting.define(null, Map.class, String.class, Boolean.class),

            Setting.define(null, Set.class, String.class),
            Setting.define(null, Set.class, DateTime.class),
            Setting.define(null, Set.class, Date.class),
            Setting.define(null, Set.class, Integer.class),
            Setting.define(null, Set.class, Float.class),
            Setting.define(null, Set.class, Double.class),
            Setting.define(null, Set.class, BigDecimal.class)

        );

        provider.getSupportedTypes().forEach(clazz -> {

                List<Setting<?>> settingsForClazz = validSettings.stream()
                    .filter(s -> s.getType().equals(clazz))
                    .collect(Collectors.toList());

                assertEquals(1, settingsForClazz.size(), settingsForClazz.toString());

                Setting<?> setting = settingsForClazz.get(0);
                assertDoesNotThrow(() -> provider.assertSupportedType(setting), setting.getType().toString());
            }
        );

    }

    @Test
    void throwsExceptionsOnAssertUnsupportedType() {

        List<Setting<?>> invalidTypeSettings = Arrays.asList(
            Setting.define(null, Set.class, Boolean.class),
            Setting.define(null, Set.class, Set.class, String.class),
            Setting.define(null, List.class, Set.class, String.class),
            Setting.define(null, Map.class, Integer.class, String.class)
        );

        invalidTypeSettings.forEach(s ->
            assertThrows(NotSupportedTypeException.class, () -> provider.assertSupportedType(s))
        );

    }

}