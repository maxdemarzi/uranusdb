package com.uranusdb.tests.server;

import com.uranusdb.server.Types;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static junit.framework.TestCase.assertTrue;

public class TypesTest {

    @Test(expected = InvocationTargetException.class)
    public void integrationTestCannotConstruct() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Types> constructor = Types.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
