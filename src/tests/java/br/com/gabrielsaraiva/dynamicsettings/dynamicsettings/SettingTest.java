package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;


import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SettingTest {

    private final String defaultValue = "My house, 123, neighborhood";
    private Setting<String> stringSetting;

    @BeforeEach
    void before() {
        stringSetting = Setting.define("address", defaultValue);
    }

    @Test
    void assertThatMissingNewValuesDontReplaceFallback() {
        stringSetting.updateValue(Optional.empty());
        Assertions.assertEquals(defaultValue, stringSetting.getValue());
    }

    @Test
    void assertThatNewValuesReplaceFallback() {
        String newValue = "Awesome house, with, 5 dogs and a big garden";
        stringSetting.updateValue(Optional.of(newValue));
        Assertions.assertEquals(newValue, stringSetting.getValue());
    }

    @Test
    void assertThatGetTypeReallyReturnsBirthType() {
        Assertions.assertEquals(String.class, stringSetting.getType());
    }

    @Test
    void failIfNotDefineModuleNames() {
        Assertions.assertThrows(RegisterSettingException.class, () -> stringSetting.getModuleName());
    }

    @Test
    void failIfDefinitionOfModuleNameTwice() {

        String moduleName = "PreciousModule";
        stringSetting.setModuleName(moduleName);
        Assertions.assertEquals(moduleName, stringSetting.getModuleName());

        Assertions.assertThrows(RegisterSettingException.class, () -> stringSetting.setModuleName("ShittyModule"));
    }

}
