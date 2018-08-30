package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

import static br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.Setting.define;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.DynamicSettingsTest.ValidSettings.Module;
import br.com.gabrielsaraiva.dynamicsettings.dynamicsettings.providers.dummy.DummyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DynamicSettingsTest {

    @BeforeEach
    void before() {

        Module.stringSetting = define("importantString");

        DummyProvider dp = new DummyProvider();

        // Required only to register those settings.
        DynamicSettings ds = new DynamicSettings(dp, 1, ValidSettings.class);

    }

    @Test
    void assertThatSettingsAreCorrectlyRegistered() {
        assertEquals(Module.class.getSimpleName(), Module.stringSetting.getModule());
    }

    @Test
    void failsIfAssignSameSettingsToSecondDynamicSettings() {

        DummyProvider dp2 = new DummyProvider();
        assertThrows(
            RegisterSettingException.class,
            () -> new DynamicSettings(dp2, 10, ValidSettings.class)
        );
    }

    @Test
    void failsIfRegisterUnsupportedSettingTypeByProvider() {

        DummyProvider dp2 = new DummyProvider();
        assertThrows(
            RegisterSettingException.class,
            () -> new DynamicSettings(dp2, 10, InvalidSettings.class)
        );
    }


    @Test
    void assertRefreshAllCallsProviderRefreshMethodEvenWithProblem() throws InterruptedException {

        DummyProvider dummyProvider = Mockito.mock(DummyProvider.class);

        Mockito.doThrow(Exception.class).when(dummyProvider).loadAll();

        DynamicSettings ds2 = new DynamicSettings(dummyProvider, 1, AnotherValidSettings.class);

        ds2.start();

        Thread.sleep(2000);

        ds2.stop();

        Thread.sleep(2000);

        // one at start + one per second * 2 = 3. But we may have some time coupling problems. with 2 we have tested.
        verify(dummyProvider, Mockito.atLeast(2)).loadAll();
        verify(dummyProvider, Mockito.atMost(3)).loadAll();

    }

    public static class AnotherValidSettings {

        public static class Module {

            public static Setting<String> anotherNameForThisProject = define("7*(15.toSoMuchLowerCase())");
        }
    }

    public static class ValidSettings {

        public static class Module {

            public static Setting<String> stringSetting = null; // This will be re-assigned every test

        }
    }

    public static class InvalidSettings {

        public static class InvalidModule {

            public static Setting<Double> pi = define(3.14);
        }
    }

}
