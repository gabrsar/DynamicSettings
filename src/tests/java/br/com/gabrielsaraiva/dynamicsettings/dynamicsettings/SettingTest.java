package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SettingTest {

    private final String defaultValue = "My house, 123, neighborhood";
    private Setting<String> stringSetting;

    @BeforeEach
    void before() {
        stringSetting = Setting.define(defaultValue);
    }

    @Test
    void assertThatMissingNewValuesDontReplaceFallback() {
        stringSetting.updateValue(Optional.empty());
        assertEquals(defaultValue, stringSetting.getValue());
    }

    @Test
    void assertThatNewValuesReplaceFallback() {
        String newValue = "Awesome house, with, 5 dogs and a big garden";
        stringSetting.updateValue(Optional.of(newValue));
        assertEquals(newValue, stringSetting.getValue());
    }

    @Test
    void assertThatGetTypeReallyReturnsBirthType() {
        assertEquals(new Clazz(String.class), stringSetting.getType());
    }

    @Test
    void failIfNotDefineModuleNames() {
        assertThrows(RegisterSettingException.class, () -> stringSetting.getModule());
    }

    @Test
    void failIfDefinitionOfModuleNameTwice() {
        String name = "PreciousSetting";
        String module = "PreciousModule";
        stringSetting.register(module, name);
        assertThrows(RegisterSettingException.class, () -> stringSetting.register(module, name));
    }

}
