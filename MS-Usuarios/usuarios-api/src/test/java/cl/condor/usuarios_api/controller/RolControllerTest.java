package cl.condor.usuarios_api.controller;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RolControllerTest {

    @Test
    void controllerIsDisabled() throws Exception {
        Constructor<RolController> constructor = RolController.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThrows(IllegalStateException.class, constructor::newInstance);
    }
}