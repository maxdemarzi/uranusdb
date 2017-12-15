package com.uranusdb.tests.server;

import com.uranusdb.server.Constants;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static junit.framework.TestCase.assertTrue;

public class ConstantsTest {

    @Test(expected = InvocationTargetException.class)
    public void integrationTestCannotConstruct() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
