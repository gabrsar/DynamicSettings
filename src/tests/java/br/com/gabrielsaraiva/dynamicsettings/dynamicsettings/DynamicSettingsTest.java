package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

import static br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Setting.define;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.DynamicSettingsTest.ValidSettings.Module;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.dummy.DummyProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DynamicSettingsTest {


    DynamicSettings ds;

    @BeforeEach
    void before() {

        Module.stringSetting = define(
            "importantString",
            "Settingszinho"
        );

        DummyProvider dp = new DummyProvider();
        ds = new DynamicSettings(dp, 1, ValidSettings.class);

    }

    @Test
    void makeSureThatSettingsAreCorrectlyRegistered() {
        Assertions.assertEquals(Module.class.getSimpleName(), Module.stringSetting.getModuleName());
    }

    @Test
    void throwsExceptionIfAssignSameSettingsToSecondDynamicSettings() {

        DummyProvider dp2 = new DummyProvider();
        Assertions.assertThrows(
            RegisterSettingException.class,
            () -> new DynamicSettings(dp2, 10, ValidSettings.class)
        );
    }

    @Test
    void refuseToRegisterUnsuportedSettingTypeByProvider() {

        DummyProvider dp2 = new DummyProvider();
        Assertions.assertThrows(
            RegisterSettingException.class,
            () -> new DynamicSettings(dp2, 10, InvalidSettings.class)
        );
    }


    @Test
    void makeSureRefreshAllCallsProviderRefreshMethodEvenWithProblem() throws InterruptedException {

        DummyProvider dummyProvider = Mockito.mock(DummyProvider.class);

        Mockito.doThrow(Exception.class).when(dummyProvider).loadAll();

        DynamicSettings ds2 = new DynamicSettings(dummyProvider, 1, AnotherValidSettings.class);

        ds2.start();

        Thread.sleep(2000);

        // one at start + one per second * 2 = 3. But we may have some time coupling problems. with 2 we have tested.
        Mockito.verify(dummyProvider, Mockito.atLeast(2)).loadAll();

    }

    public static class AnotherValidSettings {

        public static class Module {

            public static Setting<String> stringSetting = define(
                "suggestedNameForThisProject",
                "7*(15.toSoMuchLowerCase())"
            );
        }
    }

    public static class ValidSettings {

        public static class Module {

            public static Setting<String> stringSetting = null; // This will be re-assigned every test

        }
    }

    public static class InvalidSettings {

        public static class InvalidModule {

            public static Setting<Double> doubleSetting = define("pi", 3.14);
        }
    }

}
