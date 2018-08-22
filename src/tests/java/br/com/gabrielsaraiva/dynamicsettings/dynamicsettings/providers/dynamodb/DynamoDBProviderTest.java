package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.dynamodb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Clazz;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Setting;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.NotSupportedTypeException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DynamoDBProviderTest {

    private final Setting<StringBuffer> invalidTypeSetting = Setting.define("invalidType", new StringBuffer());
    private final Setting<String> stringSetting = Setting.define("stringSetting", "this is a string");
    private final Setting<Integer> integerSetting = Setting.define("integerSetting", 123);
    private final Setting<Float> floatSetting = Setting.define("floatSetting", 123.4f);
    private final Setting<Double> doubleSetting = Setting.define("doubleSetting", 123.45);
    private final Setting<Boolean> booleanSetting = Setting.define("booleanSetting", true);
    private final Setting<BigDecimal> bigDecimalSetting = Setting.define("bigDecimal", new BigDecimal("123.45678"));
    private final Setting<List<String>> listString = Setting
        .define("listString", Arrays.asList("oi", "tchau"), List.class, String.class);


    private final AmazonDynamoDB dynamoDB = mock(AmazonDynamoDB.class);
    private final DynamoDBProvider provider = new DynamoDBProvider(dynamoDB, "TestTable");


    @Test
    void currentSupportedTypesStillWorking() throws NotSupportedTypeException {
        assertEquals(29, provider.getSupportedTypes().size());
    }


    @Test
    void x() throws NotSupportedTypeException {
        provider.assertSupportedType(listString);
    }

    @Test
    void throwsExceptionsOnAssertUnsupportedType() {

        assertThrows(
            NotSupportedTypeException.class,
            () -> provider.assertSupportedType(invalidTypeSetting)
        );

    }

}